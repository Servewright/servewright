import 'dart:convert';

import 'package:http/http.dart' as http;
import 'package:servewright_flutter/src/transition.dart';
import 'package:servewright_flutter/src/types.dart';

class ActionResponse {
  const ActionResponse({this.view, this.transition});

  final ServewrightView? view;
  final ServewrightTransition? transition;
}

class ActionClient {
  ActionClient({
    this.actionUrl = '/servewright/action',
    this.viewUrl = '/servewright/view',
    http.Client? client,
  }) : _client = client ?? http.Client();

  final String actionUrl;
  final String viewUrl;
  final http.Client _client;

  Future<ActionResponse> postAction(ServewrightAction action) async {
    final response = await _client.post(
      Uri.parse(actionUrl),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode(action.toJson()),
    );

    if (response.statusCode < 200 || response.statusCode >= 300) {
      throw Exception('Action failed: HTTP ${response.statusCode}');
    }

    final document = jsonDecode(response.body) as Map<String, dynamic>;
    if (document.containsKey('transition')) {
      return ActionResponse(
        transition: ServewrightTransition.fromJson(
          document['transition'] as Map<String, dynamic>,
        ),
      );
    }
    return ActionResponse(
      view: ServewrightView.fromJson(document['view'] as Map<String, dynamic>),
    );
  }

  Future<ServewrightView> fetchView(String screen) async {
    final response = await _client.get(Uri.parse('$viewUrl/$screen'));
    if (response.statusCode < 200 || response.statusCode >= 300) {
      throw Exception('View fetch failed: HTTP ${response.statusCode}');
    }
    return ServewrightView.fromJson(jsonDecode(response.body) as Map<String, dynamic>);
  }
}

class ServewrightAction {
  const ServewrightAction({
    required this.type,
    required this.target,
    required this.screen,
    required this.stateVersion,
    required this.payload,
  });

  final String type;
  final String target;
  final String screen;
  final int stateVersion;
  final Map<String, String> payload;

  Map<String, dynamic> toJson() => {
        'type': type,
        'target': target,
        'screen': screen,
        'stateVersion': stateVersion,
        'payload': payload,
      };
}
