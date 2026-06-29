import 'package:flutter_test/flutter_test.dart';
import 'package:servewright_flutter/servewright_flutter.dart';

void main() {
  test('validateTextInput rejects invalid email', () {
    const node = ServewrightNode(
      id: 'email',
      type: 'TextInput',
      props: {
        'label': 'Email',
        'required': true,
        'pattern': r'^[^@]+@[^@]+\.[^@]+$',
      },
    );

    final errors = BindingValidation.validateTextInput(node, 'bad');
    expect(errors, isNotEmpty);
  });

  test('effectiveTrigger switches to onChange after invalid field', () {
    const node = ServewrightNode(
      id: 'email',
      type: 'TextInput',
      props: {'label': 'Email', 'trigger': 'onBlur'},
    );

    expect(BindingTree.effectiveTrigger(node, false), 'onBlur');
    expect(BindingTree.effectiveTrigger(node, true), 'onChange');
  });
}
