import 'package:flutter_test/flutter_test.dart';
import 'package:servewright_flutter/servewright_flutter.dart';

void main() {
  test('applyTransition attaches setError to target field', () {
    const view = ServewrightView(
      servewrightVersion: '1.0',
      schemaVersion: '0.1.0',
      screen: 'demo-form',
      stateVersion: 0,
      root: ServewrightNode(
        id: 'signup-form',
        type: 'Form',
        props: {'actionTarget': 'signup'},
        children: [
          ServewrightNode(
            id: 'email',
            type: 'TextInput',
            props: {'label': 'Email', 'value': ''},
          ),
        ],
      ),
    );

    final updated = TransitionApplier.apply(
      view,
      ServewrightTransition(
        basedOn: 0,
        stateVersion: 1,
        patches: [
          {
            'op': 'setError',
            'target': 'email',
            'errors': ['Invalid format'],
          },
        ],
      ),
    );

    expect(updated.root.children.first.props['errors'], ['Invalid format']);
  });

  test('dirty field keeps local value on replace patch', () {
    const view = ServewrightView(
      servewrightVersion: '1.0',
      schemaVersion: '0.1.0',
      screen: 'demo-form',
      stateVersion: 0,
      root: ServewrightNode(
        id: 'email',
        type: 'TextInput',
        props: {'label': 'Email', 'value': ''},
      ),
    );

    final updated = TransitionApplier.apply(
      view,
      ServewrightTransition(
        basedOn: 0,
        stateVersion: 1,
        patches: [
          {
            'op': 'replace',
            'target': 'email',
            'node': {
              'id': 'email',
              'type': 'TextInput',
              'props': {'label': 'Email', 'value': 'server'},
            },
          },
        ],
      ),
      dirtyFields: {'email'},
    );

    expect(updated.root.props['value'], '');
  });

  test('applyTransition inserts node at index', () {
    const view = ServewrightView(
      servewrightVersion: '1.0',
      schemaVersion: '0.1.0',
      screen: 'demo-form',
      stateVersion: 0,
      root: ServewrightNode(
        id: 'group',
        type: 'Group',
        props: {'label': 'Fields'},
        children: [
          ServewrightNode(
            id: 'email',
            type: 'TextInput',
            props: {'label': 'Email', 'value': ''},
          ),
        ],
      ),
    );

    final updated = TransitionApplier.apply(
      view,
      ServewrightTransition(
        basedOn: 0,
        stateVersion: 1,
        patches: [
          {
            'op': 'insert',
            'parent': 'group',
            'index': 1,
            'node': {
              'id': 'username',
              'type': 'TextInput',
              'props': {'label': 'Username', 'value': ''},
            },
          },
        ],
      ),
    );

    expect(updated.root.children.length, 2);
    expect(updated.root.children[1].id, 'username');
  });

  test('applyTransition removes node by id', () {
    const view = ServewrightView(
      servewrightVersion: '1.0',
      schemaVersion: '0.1.0',
      screen: 'demo-form',
      stateVersion: 0,
      root: ServewrightNode(
        id: 'group',
        type: 'Group',
        props: {'label': 'Fields'},
        children: [
          ServewrightNode(
            id: 'email',
            type: 'TextInput',
            props: {'label': 'Email', 'value': ''},
          ),
          ServewrightNode(
            id: 'username',
            type: 'TextInput',
            props: {'label': 'Username', 'value': ''},
          ),
        ],
      ),
    );

    final updated = TransitionApplier.apply(
      view,
      ServewrightTransition(
        basedOn: 0,
        stateVersion: 1,
        patches: [
          {'op': 'remove', 'target': 'username'},
        ],
      ),
    );

    expect(updated.root.children.length, 1);
    expect(updated.root.children.first.id, 'email');
  });

  test('throws TransitionDesyncError when basedOn mismatches stateVersion', () {
    const staleView = ServewrightView(
      servewrightVersion: '1.0',
      schemaVersion: '0.1.0',
      screen: 'demo-form',
      stateVersion: 1,
      root: ServewrightNode(
        id: 'email',
        type: 'TextInput',
        props: {'label': 'Email', 'value': ''},
      ),
    );

    expect(
      () => TransitionApplier.apply(
        staleView,
        ServewrightTransition(
          basedOn: 0,
          stateVersion: 2,
          patches: [
            {
              'op': 'setError',
              'target': 'email',
              'errors': ['Stale'],
            },
          ],
        ),
      ),
      throwsA(isA<TransitionDesyncError>()),
    );
  });
}
