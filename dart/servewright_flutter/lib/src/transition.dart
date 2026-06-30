import 'package:servewright_flutter/src/types.dart';

class TransitionDesyncError implements Exception {
  TransitionDesyncError(this.expectedBasedOn, this.actualStateVersion);

  final int expectedBasedOn;
  final int actualStateVersion;

  @override
  String toString() =>
      'Transition basedOn=$expectedBasedOn does not match client stateVersion=$actualStateVersion';
}

class ServewrightTransition {
  const ServewrightTransition({
    required this.basedOn,
    required this.stateVersion,
    required this.patches,
  });

  final int basedOn;
  final int stateVersion;
  final List<Map<String, dynamic>> patches;

  factory ServewrightTransition.fromJson(Map<String, dynamic> json) {
    return ServewrightTransition(
      basedOn: json['basedOn'] as int,
      stateVersion: json['stateVersion'] as int,
      patches: (json['patches'] as List<dynamic>).cast<Map<String, dynamic>>(),
    );
  }
}

class TransitionApplier {
  static ServewrightView apply(
    ServewrightView view,
    ServewrightTransition transition, {
    Set<String> dirtyFields = const {},
  }) {
    if (transition.basedOn != view.stateVersion) {
      throw TransitionDesyncError(transition.basedOn, view.stateVersion);
    }

    var root = view.root;
    for (final patch in transition.patches) {
      root = _applyPatch(root, patch, dirtyFields);
    }

    return ServewrightView(
      servewrightVersion: view.servewrightVersion,
      schemaVersion: view.schemaVersion,
      screen: view.screen,
      stateVersion: transition.stateVersion,
      root: root,
    );
  }

  static Set<String> collectDirtyFields(
    ServewrightView view,
    Map<String, String> localValues,
  ) {
    final dirty = <String>{};
    for (final entry in localValues.entries) {
      final node = _findNodeById(view.root, entry.key);
      final serverValue = '${node?.props['value'] ?? ''}';
      if (serverValue != entry.value) {
        dirty.add(entry.key);
      }
    }
    return dirty;
  }

  static ServewrightNode _applyPatch(
    ServewrightNode node,
    Map<String, dynamic> patch,
    Set<String> dirtyFields,
  ) {
    final op = patch['op'] as String;
    switch (op) {
      case 'replace':
        final target = patch['target'] as String;
        final replacement = ServewrightNode.fromJson(patch['node'] as Map<String, dynamic>);
        if (node.id == target) {
          if (dirtyFields.contains(target) && _isInput(replacement)) {
            return ServewrightNode(
              id: replacement.id,
              type: replacement.type,
              props: {...replacement.props, 'value': node.props['value']},
              children: replacement.children,
            );
          }
          return replacement;
        }
        return ServewrightNode(
          id: node.id,
          type: node.type,
          props: node.props,
          children: node.children
              .map((child) => _applyPatch(child, patch, dirtyFields))
              .toList(),
        );
      case 'setError':
        final target = patch['target'] as String;
        if (node.id == target) {
          return ServewrightNode(
            id: node.id,
            type: node.type,
            props: {...node.props, 'errors': patch['errors']},
            children: node.children,
          );
        }
        return ServewrightNode(
          id: node.id,
          type: node.type,
          props: node.props,
          children: node.children
              .map((child) => _applyPatch(child, patch, dirtyFields))
              .toList(),
        );
      case 'setLoading':
        final target = patch['target'] as String;
        if (node.id == target) {
          final props = Map<String, dynamic>.from(node.props);
          if (patch['loading'] == true) {
            props['loading'] = true;
          } else {
            props.remove('loading');
          }
          return ServewrightNode(id: node.id, type: node.type, props: props, children: node.children);
        }
        return ServewrightNode(
          id: node.id,
          type: node.type,
          props: node.props,
          children: node.children
              .map((child) => _applyPatch(child, patch, dirtyFields))
              .toList(),
        );
      case 'insert':
        final parent = patch['parent'] as String;
        final index = patch['index'] as int;
        final inserted = ServewrightNode.fromJson(patch['node'] as Map<String, dynamic>);
        if (node.id == parent) {
          final children = List<ServewrightNode>.from(node.children);
          children.insert(index, inserted);
          return ServewrightNode(
            id: node.id,
            type: node.type,
            props: node.props,
            children: children,
          );
        }
        return ServewrightNode(
          id: node.id,
          type: node.type,
          props: node.props,
          children: node.children
              .map((child) => _applyPatch(child, patch, dirtyFields))
              .toList(),
        );
      case 'remove':
        final target = patch['target'] as String;
        if (node.id == target) {
          return node;
        }
        return ServewrightNode(
          id: node.id,
          type: node.type,
          props: node.props,
          children: node.children
              .where((child) => child.id != target)
              .map((child) => _applyPatch(child, patch, dirtyFields))
              .toList(),
        );
      default:
        return node;
    }
  }

  static bool _isInput(ServewrightNode node) =>
      node.type == 'TextInput' || node.type == 'Select' || node.type == 'Checkbox';

  static ServewrightNode? _findNodeById(ServewrightNode node, String id) {
    if (node.id == id) {
      return node;
    }
    for (final child in node.children) {
      final found = _findNodeById(child, id);
      if (found != null) {
        return found;
      }
    }
    return null;
  }
}
