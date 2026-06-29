import 'package:flutter/material.dart';
import 'package:servewright_flutter/servewright_flutter.dart';

void registerMaterialPrimitives(Registry registry) {
  registry.register('Text', buildTextPrimitive);
}

Widget buildTextPrimitive(ServewrightNode node) {
  final content = node.props['content'] as String? ?? '';
  final emphasis = node.props['emphasis'] as String? ?? 'body';

  final TextStyle style;
  switch (emphasis) {
    case 'heading':
      style = const TextStyle(fontSize: 24, fontWeight: FontWeight.bold);
    case 'caption':
      style = const TextStyle(fontSize: 12);
    case 'muted':
      style = const TextStyle(color: Colors.grey);
    case 'body':
    default:
      style = const TextStyle(fontSize: 16);
  }

  return Text(
    content,
    key: ValueKey('text-${node.id}'),
    style: style,
  );
}
