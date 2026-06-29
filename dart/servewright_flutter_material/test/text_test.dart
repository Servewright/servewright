import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:servewright_flutter/servewright_flutter.dart';
import 'package:servewright_flutter_material/servewright_flutter_material.dart';

void main() {
  testWidgets('material Text maps heading emphasis', (tester) async {
    final registry = createRegistry();
    registerMaterialPrimitives(registry);
    final renderer = createRenderer(registry);

    await tester.pumpWidget(
      MaterialApp(
        home: Scaffold(
          body: renderer.render(
            const ServewrightView(
              servewrightVersion: '1.0',
              schemaVersion: '0.1.0',
              screen: 'hello',
              stateVersion: 0,
              root: ServewrightNode(
                id: 'title',
                type: 'Text',
                props: {'content': 'Bonjour', 'emphasis': 'heading'},
              ),
            ),
          ),
        ),
      ),
    );

    final text = tester.widget<Text>(find.text('Bonjour'));
    expect(text.style?.fontSize, 24);
  });
}
