/// Servewright view contract types.
library;

typedef JsonObject = Map<String, dynamic>;

class ServewrightNode {
  const ServewrightNode({
    required this.id,
    required this.type,
    required this.props,
    this.children = const [],
  });

  final String id;
  final String type;
  final JsonObject props;
  final List<ServewrightNode> children;

  factory ServewrightNode.fromJson(Map<String, dynamic> json) {
    return ServewrightNode(
      id: json['id'] as String,
      type: json['type'] as String,
      props: Map<String, dynamic>.from(json['props'] as Map),
      children: (json['children'] as List<dynamic>? ?? const [])
          .map((child) => ServewrightNode.fromJson(child as Map<String, dynamic>))
          .toList(),
    );
  }
}

class ServewrightView {
  const ServewrightView({
    required this.servewrightVersion,
    required this.schemaVersion,
    required this.screen,
    required this.stateVersion,
    required this.root,
  });

  final String servewrightVersion;
  final String schemaVersion;
  final String screen;
  final int stateVersion;
  final ServewrightNode root;

  factory ServewrightView.fromJson(Map<String, dynamic> json) {
    return ServewrightView(
      servewrightVersion: json['servewrightVersion'] as String,
      schemaVersion: json['schemaVersion'] as String,
      screen: json['screen'] as String,
      stateVersion: json['stateVersion'] as int,
      root: ServewrightNode.fromJson(json['root'] as Map<String, dynamic>),
    );
  }
}
