import 'package:flutter/material.dart';
import 'package:servewright_flutter/servewright_flutter.dart';

void registerMaterialPrimitives(Registry registry) {
  registry.register('Text', buildTextPrimitive);
  registry.register('Container', buildContainerPrimitive);
  registry.register('Form', buildFormPrimitive);
  registry.register('Group', buildGroupPrimitive);
  registry.register('TextInput', buildTextInputPrimitive);
  registry.register('Select', buildSelectPrimitive);
  registry.register('Checkbox', buildCheckboxPrimitive);
  registry.register('Button', buildButtonPrimitive);
  registry.register('Stat', buildStatPrimitive);
  registry.register('Table', buildTablePrimitive);
}

Widget buildTextPrimitive(ServewrightNode node, RenderContext ctx) {
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

  return Text(content, key: ValueKey('text-${node.id}'), style: style);
}

Widget buildContainerPrimitive(ServewrightNode node, RenderContext ctx) {
  final layout = node.props['layout'] as String? ?? 'vertical';
  return Column(
    key: ValueKey('container-${node.id}'),
    crossAxisAlignment: CrossAxisAlignment.start,
    children: layout == 'horizontal'
        ? [Row(children: ctx.renderChildren(node.children))]
        : ctx.renderChildren(node.children),
  );
}

Widget buildFormPrimitive(ServewrightNode node, RenderContext ctx) {
  return Column(
    key: ValueKey('form-${node.id}'),
    crossAxisAlignment: CrossAxisAlignment.start,
    children: ctx.renderChildren(node.children),
  );
}

Widget buildGroupPrimitive(ServewrightNode node, RenderContext ctx) {
  final label = node.props['label'] as String?;
  return Column(
    key: ValueKey('group-${node.id}'),
    crossAxisAlignment: CrossAxisAlignment.start,
    children: [
      if (label != null) Text(label, style: const TextStyle(fontWeight: FontWeight.bold)),
      ...ctx.renderChildren(node.children),
    ],
  );
}

Widget buildTextInputPrimitive(ServewrightNode node, RenderContext ctx) {
  final label = node.props['label'] as String? ?? '';
  final value = node.props['value'] as String? ?? '';
  final placeholder = node.props['placeholder'] as String?;
  return Column(
    key: ValueKey('text-input-${node.id}'),
    crossAxisAlignment: CrossAxisAlignment.start,
    children: [
      Text(label),
      TextField(
        controller: TextEditingController(text: value),
        readOnly: true,
        decoration: InputDecoration(hintText: placeholder),
      ),
    ],
  );
}

Widget buildSelectPrimitive(ServewrightNode node, RenderContext ctx) {
  final label = node.props['label'] as String? ?? '';
  final value = node.props['value'] as String?;
  final options = (node.props['options'] as List<dynamic>? ?? [])
      .cast<Map<String, dynamic>>();
  return Column(
    key: ValueKey('select-${node.id}'),
    crossAxisAlignment: CrossAxisAlignment.start,
    children: [
      Text(label),
      DropdownButton<String>(
        value: value ?? (options.isNotEmpty ? options.first['value'] as String? : null),
        items: options
            .map(
              (option) => DropdownMenuItem<String>(
                value: option['value'] as String,
                child: Text(option['label'] as String),
              ),
            )
            .toList(),
        onChanged: null,
      ),
    ],
  );
}

Widget buildCheckboxPrimitive(ServewrightNode node, RenderContext ctx) {
  final label = node.props['label'] as String? ?? '';
  final checked = node.props['checked'] as bool? ?? false;
  return CheckboxListTile(
    key: ValueKey('checkbox-${node.id}'),
    title: Text(label),
    value: checked,
    onChanged: null,
  );
}

Widget buildButtonPrimitive(ServewrightNode node, RenderContext ctx) {
  final label = node.props['label'] as String? ?? '';
  return ElevatedButton(
    key: ValueKey('button-${node.id}'),
    onPressed: null,
    child: Text(label),
  );
}

Widget buildStatPrimitive(ServewrightNode node, RenderContext ctx) {
  final label = node.props['label'] as String? ?? '';
  final value = node.props['value'] as String? ?? '';
  final delta = node.props['delta'] as String?;
  return Column(
    key: ValueKey('stat-${node.id}'),
    crossAxisAlignment: CrossAxisAlignment.start,
    children: [
      Text(label, style: const TextStyle(fontSize: 12)),
      Text(value, style: const TextStyle(fontSize: 24, fontWeight: FontWeight.bold)),
      if (delta != null) Text(delta),
    ],
  );
}

Widget buildTablePrimitive(ServewrightNode node, RenderContext ctx) {
  final columns = (node.props['columns'] as List<dynamic>? ?? [])
      .cast<Map<String, dynamic>>();
  final rows = (node.props['rows'] as List<dynamic>? ?? [])
      .cast<Map<String, dynamic>>();

  return DataTable(
    key: ValueKey('table-${node.id}'),
    columns: columns
        .map(
          (column) => DataColumn(
            label: Text(column['label'] as String),
          ),
        )
        .toList(),
    rows: rows
        .map(
          (row) {
            final cells = row['cells'] as Map<String, dynamic>? ?? {};
            return DataRow(
              cells: columns
                  .map(
                    (column) => DataCell(
                      Text('${cells[column['key']]}'),
                    ),
                  )
                  .toList(),
            );
          },
        )
        .toList(),
  );
}
