import 'dart:convert';

import 'package:http/http.dart' as http;
import 'package:servewright_flutter/servewright_flutter.dart';

class HelloViewRepository {
  HelloViewRepository({
    http.Client? client,
    this.endpoint = const String.fromEnvironment(
      'SERVEWRIGHT_URL',
      defaultValue: 'http://localhost:8080/servewright/view/hello',
    ),
  }) : _client = client ?? http.Client();

  final http.Client _client;
  final String endpoint;

  Future<ServewrightView> fetchHelloView() async {
    final response = await _client.get(Uri.parse(endpoint));
    if (response.statusCode != 200) {
      throw Exception('HTTP ${response.statusCode}');
    }
    return ServewrightView.fromJson(
      jsonDecode(response.body) as Map<String, dynamic>,
    );
  }
}
