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
  return _FormWidget(node: node, ctx: ctx);
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
  return _TextInputWidget(node: node);
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
  return _ButtonWidget(node: node);
}

Widget buildStatPrimitive(ServewrightNode node, RenderContext ctx) {
  final label = node.props['label'] as String? ?? '';
  final value = node.props['value'] as String? ?? '';
  final delta = node.props['delta'] as String?;
  return Column(
    key: ValueKey('stat-${node.id}'),
    crossAxisAlignment: CrossAxisAlignment.start,
    children: [
      Text(label, style: const TextStyle(fontWeight: FontWeight.bold)),
      Text(value),
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
        .map((column) => DataColumn(label: Text(column['label'] as String)))
        .toList(),
    rows: rows
        .map(
          (row) => DataRow(
            cells: columns
                .map((column) {
                  final cells = row['cells'] as Map<String, dynamic>? ?? {};
                  return DataCell(Text('${cells[column['key']]}'));
                })
                .toList(),
          ),
        )
        .toList(),
  );
}

class _FormWidget extends StatelessWidget {
  const _FormWidget({required this.node, required this.ctx});

  final ServewrightNode node;
  final RenderContext ctx;

  @override
  Widget build(BuildContext context) {
    final actionTarget = node.props['actionTarget'] as String? ?? '';
    final binding = ServewrightBinding.maybeOf(context);

    return Column(
      key: ValueKey('form-${node.id}'),
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        ...ctx.renderChildren(node.children),
        if (binding != null)
          const SizedBox.shrink()
        else
          const SizedBox.shrink(),
      ],
    );
  }
}

class _TextInputWidget extends StatefulWidget {
  const _TextInputWidget({required this.node});

  final ServewrightNode node;

  @override
  State<_TextInputWidget> createState() => _TextInputWidgetState();
}

class _TextInputWidgetState extends State<_TextInputWidget> {
  late final TextEditingController _controller;

  @override
  void initState() {
    super.initState();
    _controller = TextEditingController(text: widget.node.props['value'] as String? ?? '');
  }

  @override
  void didUpdateWidget(covariant _TextInputWidget oldWidget) {
    super.didUpdateWidget(oldWidget);
    final binding = ServewrightBinding.maybeOf(context);
    final nextValue = binding?.valueFor(widget.node.id) ??
        widget.node.props['value'] as String? ??
        '';
    if (_controller.text != nextValue) {
      _controller.text = nextValue;
    }
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final binding = ServewrightBinding.maybeOf(context);
    final label = widget.node.props['label'] as String? ?? '';
    final placeholder = widget.node.props['placeholder'] as String?;
    final interactive = binding != null;
    final errors = binding?.errorsFor(widget.node.id) ??
        (widget.node.props['errors'] as List<dynamic>? ?? const []).cast<String>();
    final validating = binding?.isValidating(widget.node.id) ??
        widget.node.props['validating'] as bool? ??
        widget.node.props['loading'] as bool? ??
        false;

    if (interactive) {
      _controller.text = binding!.valueFor(widget.node.id);
    }

    return Column(
      key: ValueKey('text-input-${widget.node.id}'),
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        TextField(
          controller: _controller,
          readOnly: !interactive,
          decoration: InputDecoration(
            labelText: label.isEmpty ? 'Text field' : label,
            hintText: placeholder,
          ),
          onChanged: interactive
              ? (value) => binding!.onFieldChange(widget.node.id, widget.node, value)
              : null,
          onEditingComplete: interactive
              ? () => binding!.onFieldBlur(
                    widget.node.id,
                    widget.node,
                    binding.primaryActionTarget,
                  )
              : null,
        ),
        if (validating) const Text('Validating…'),
        for (final error in errors) Text(error, style: const TextStyle(color: Colors.red)),
      ],
    );
  }
}

class _ButtonWidget extends StatelessWidget {
  const _ButtonWidget({required this.node});

  final ServewrightNode node;

  @override
  Widget build(BuildContext context) {
    final binding = ServewrightBinding.maybeOf(context);
    final label = node.props['label'] as String? ?? '';
    final role = node.props['role'] as String? ?? 'button';

    return ElevatedButton(
      key: ValueKey('button-${node.id}'),
      onPressed: binding == null
          ? null
          : role == 'submit'
              ? () => binding.submitForm(binding.primaryActionTarget)
              : null,
      child: Text(label),
    );
  }
}
