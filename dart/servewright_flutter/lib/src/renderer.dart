import 'package:flutter/widgets.dart';
import 'package:servewright_flutter/src/types.dart';

class RenderContext {
  const RenderContext({required this.renderChild});

  final Widget Function(ServewrightNode node) renderChild;

  List<Widget> renderChildren(List<ServewrightNode> nodes) =>
      nodes.map(renderChild).toList();
}

typedef PrimitiveBuilder = Widget Function(ServewrightNode node, RenderContext ctx);

class Registry {
  final Map<String, PrimitiveBuilder> _builders = {};

  void register(String type, PrimitiveBuilder builder) {
    _builders[type] = builder;
  }

  PrimitiveBuilder? resolve(String type) => _builders[type];
}

class Renderer {
  Renderer(this.registry);

  final Registry registry;

  Widget render(ServewrightView view) => _renderNode(view.root);

  Widget _renderNode(ServewrightNode node) {
    final builder = registry.resolve(node.type);
    if (builder == null) {
      return _unknownPlaceholder(node);
    }
    return builder(node, RenderContext(renderChild: _renderNode));
  }

  Widget _unknownPlaceholder(ServewrightNode node) {
    return SizedBox(
      key: ValueKey('unknown-${node.id}'),
      child: Text('Unknown primitive: ${node.type}'),
    );
  }
}

Registry createRegistry() => Registry();

Renderer createRenderer(Registry registry) => Renderer(registry);
