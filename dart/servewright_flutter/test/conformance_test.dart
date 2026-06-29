import 'dart:convert';
import 'dart:io';

import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:servewright_flutter/servewright_flutter.dart';
import 'package:servewright_flutter_material/servewright_flutter_material.dart';

void main() {
  final casesDir = Directory('../../conformance/cases');
  final registry = createRegistry();
  registerMaterialPrimitives(registry);
  final renderer = createRenderer(registry);

  for (final file in casesDir
      .listSync()
      .whereType<File>()
      .where((file) => file.path.endsWith('.json'))) {
    final testCase = jsonDecode(file.readAsStringSync()) as Map<String, dynamic>;
    final primitive = testCase['primitive'] as String;
    final rootJson = testCase['root'] as Map<String, dynamic>;
    final expected = (testCase['expect'] as Map<String, dynamic>)['contains'] as List<dynamic>;

    testWidgets('conformance flutter renders $primitive (${file.uri.pathSegments.last})', (tester) async {
      final view = ServewrightView(
        servewrightVersion: '1.0',
        schemaVersion: '0.1.0',
        screen: 'conformance',
        stateVersion: 0,
        root: ServewrightNode.fromJson(rootJson),
      );

      await tester.pumpWidget(
        MaterialApp(home: Scaffold(body: renderer.render(view))),
      );

      for (final text in expected) {
        expect(find.text('$text'), findsWidgets);
      }
    });
  }
}
