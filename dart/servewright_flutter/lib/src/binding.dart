import 'package:servewright_flutter/src/types.dart';

typedef FieldTrigger = String;

class BindingValidation {
  static List<String> validateTextInput(ServewrightNode node, String value) {
    final props = node.props;
    final errors = <String>[];

    if (props['required'] == true && value.trim().isEmpty) {
      errors.add('Required');
    }

    final minLength = props['minLength'];
    if (minLength is num && value.length < minLength.toInt()) {
      errors.add('Minimum length is ${minLength.toInt()}');
    }

    final pattern = props['pattern'];
    if (pattern is String && value.isNotEmpty) {
      final regex = RegExp(pattern);
      if (!regex.hasMatch(value)) {
        errors.add('Invalid format');
      }
    }

    return errors;
  }

  static List<String> validateInputNode(ServewrightNode node, String value) {
    if (node.type == 'TextInput') {
      return validateTextInput(node, value);
    }
    return const [];
  }
}

class BindingTree {
  static ServewrightNode? findFormByActionTarget(ServewrightNode root, String target) {
    if (root.type == 'Form' && root.props['actionTarget'] == target) {
      return root;
    }
    for (final child in root.children) {
      final found = findFormByActionTarget(child, target);
      if (found != null) {
        return found;
      }
    }
    return null;
  }

  static String? primaryActionTarget(ServewrightNode root) {
    if (root.type == 'Form') {
      return root.props['actionTarget'] as String?;
    }
    for (final child in root.children) {
      final found = primaryActionTarget(child);
      if (found != null) {
        return found;
      }
    }
    return null;
  }

  static List<ServewrightNode> collectInputNodes(ServewrightNode root) {
    final inputs = <ServewrightNode>[];
    _walk(root, inputs);
    return inputs;
  }

  static void _walk(ServewrightNode node, List<ServewrightNode> inputs) {
    if (node.type == 'TextInput' || node.type == 'Select' || node.type == 'Checkbox') {
      inputs.add(node);
    }
    for (final child in node.children) {
      _walk(child, inputs);
    }
  }

  static Map<String, String> extractInitialValues(ServewrightNode root) {
    final values = <String, String>{};
    for (final field in collectInputNodes(root)) {
      if (field.type == 'Checkbox') {
        values[field.id] = '${field.props['checked'] ?? false}';
      } else {
        values[field.id] = '${field.props['value'] ?? ''}';
      }
    }
    return values;
  }

  static Map<String, String> collectFormPayload(
    ServewrightNode formRoot,
    Map<String, String> values,
  ) {
    final payload = <String, String>{};
    for (final field in collectInputNodes(formRoot)) {
      payload[field.id] = values[field.id] ?? '${field.props['value'] ?? ''}';
    }
    return payload;
  }

  static ServewrightNode mergeLocalValues(
    ServewrightNode node,
    Map<String, String> values,
  ) {
    final props = Map<String, dynamic>.from(node.props);
    if (node.type == 'TextInput' || node.type == 'Select') {
      if (values.containsKey(node.id)) {
        props['value'] = values[node.id];
      }
    } else if (node.type == 'Checkbox' && values.containsKey(node.id)) {
      props['checked'] = values[node.id] == 'true';
    }

    return ServewrightNode(
      id: node.id,
      type: node.type,
      props: props,
      children: node.children.map((child) => mergeLocalValues(child, values)).toList(),
    );
  }

  static FieldTrigger effectiveTrigger(ServewrightNode node, bool forceOnChange) {
    if (forceOnChange) {
      return 'onChange';
    }
    return (node.props['trigger'] as String?) ?? 'onBlur';
  }

  static List<String> readFieldErrors(ServewrightNode root, String fieldId) {
    final errors = root.props['errors'];
    if (root.id == fieldId && errors is List) {
      return errors.cast<String>();
    }
    for (final child in root.children) {
      final found = readFieldErrors(child, fieldId);
      if (found.isNotEmpty) {
        return found;
      }
    }
    return const [];
  }
}
