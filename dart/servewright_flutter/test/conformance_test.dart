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
      .where((entry) => entry.path.endsWith('.json'))) {
    final testCase = jsonDecode(file.readAsStringSync()) as Map<String, dynamic>;
    final expectations = testCase['expect'] as Map<String, dynamic>;
    final expected = (expectations['contains'] as List<dynamic>?) ?? [];
    final notExpected = (expectations['notContains'] as List<dynamic>?) ?? [];
    final semanticsLabels = (expectations['semanticsLabels'] as List<dynamic>?) ?? [];
    final fileName = file.uri.pathSegments.last;

    if (testCase.containsKey('initialView')) {
      testWidgets('conformance flutter applies transition ($fileName)', (tester) async {
        var view = ServewrightView.fromJson(testCase['initialView'] as Map<String, dynamic>);
        for (final transitionJson in testCase['transitions'] as List<dynamic>) {
          final transition = ServewrightTransition.fromJson(transitionJson as Map<String, dynamic>);
          view = TransitionApplier.apply(view, transition);
        }

        await tester.pumpWidget(
          MaterialApp(home: Scaffold(body: renderer.render(view))),
        );

        for (final text in expected) {
          expect(find.textContaining('$text'), findsWidgets);
        }
        for (final text in notExpected) {
          expect(find.text('$text'), findsNothing);
        }
        for (final label in semanticsLabels) {
          expect(find.bySemanticsLabel('$label'), findsWidgets);
        }
      });
      continue;
    }

    final primitive = testCase['primitive'] as String;
    final rootJson = testCase['root'] as Map<String, dynamic>;

    testWidgets('conformance flutter renders $primitive ($fileName)', (tester) async {
      final view = ServewrightView(
        servewrightVersion: '1.0',
        schemaVersion: testCase['schemaVersion'] as String? ?? '0.1.0',
        screen: 'conformance',
        stateVersion: 0,
        root: ServewrightNode.fromJson(rootJson),
      );

      await tester.pumpWidget(
        MaterialApp(home: Scaffold(body: renderer.render(view))),
      );

      for (final text in expected) {
        expect(find.textContaining('$text'), findsWidgets);
      }
      for (final text in notExpected) {
        expect(find.text('$text'), findsNothing);
      }
      for (final label in semanticsLabels) {
        expect(find.bySemanticsLabel('$label'), findsWidgets);
      }
    });
  }
}
