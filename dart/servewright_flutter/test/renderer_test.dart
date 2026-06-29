import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:servewright_flutter/servewright_flutter.dart';
import 'package:servewright_flutter_material/servewright_flutter_material.dart';

void main() {
  testWidgets('renders Text primitive content', (tester) async {
    final registry = createRegistry();
    registerMaterialPrimitives(registry);
    final renderer = createRenderer(registry);
    const view = ServewrightView(
      servewrightVersion: '1.0',
      schemaVersion: '0.1.0',
      screen: 'hello',
      stateVersion: 0,
      root: ServewrightNode(
        id: 'greeting',
        type: 'Text',
        props: {'content': 'Bonjour'},
      ),
    );

    await tester.pumpWidget(
      Directionality(
        textDirection: TextDirection.ltr,
        child: renderer.render(view),
      ),
    );

    expect(find.text('Bonjour'), findsOneWidget);
  });

  testWidgets('unknown primitive renders placeholder without crashing', (tester) async {
    final renderer = createRenderer(createRegistry());
    const view = ServewrightView(
      servewrightVersion: '1.0',
      schemaVersion: '0.1.0',
      screen: 'hello',
      stateVersion: 0,
      root: ServewrightNode(
        id: 'mystery',
        type: 'FutureWidget',
        props: {},
      ),
    );

    await tester.pumpWidget(
      Directionality(
        textDirection: TextDirection.ltr,
        child: renderer.render(view),
      ),
    );

    expect(find.text('Unknown primitive: FutureWidget'), findsOneWidget);
  });

  testWidgets('renders nested composition hierarchy', (tester) async {
    final registry = createRegistry();
    registerMaterialPrimitives(registry);
    final renderer = createRenderer(registry);
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
            id: 'personal-group',
            type: 'Group',
            props: {'label': 'Personal'},
            children: [
              ServewrightNode(
                id: 'email',
                type: 'TextInput',
                props: {'label': 'Email', 'placeholder': 'you@example.com'},
              ),
              ServewrightNode(
                id: 'submit',
                type: 'Button',
                props: {'label': 'Submit', 'role': 'submit'},
              ),
            ],
          ),
        ],
      ),
    );

    await tester.pumpWidget(
      MaterialApp(
        home: Scaffold(body: renderer.render(view)),
      ),
    );

    expect(find.text('Personal'), findsOneWidget);
    expect(find.text('Email'), findsOneWidget);
    expect(find.text('Submit'), findsOneWidget);
  });
}
