import 'dart:convert';

import 'package:http/http.dart' as http;
import 'package:servewright_flutter/servewright_flutter.dart';

class SignupViewRepository {
  SignupViewRepository({http.Client? client}) : _client = client ?? http.Client();

  final http.Client _client;

  Future<ServewrightView> fetchSignupView({String baseUrl = 'http://localhost:8080'}) async {
    final response = await _client.get(Uri.parse('$baseUrl/servewright/view/demo-form'));
    if (response.statusCode != 200) {
      throw Exception('Failed to load signup view: HTTP ${response.statusCode}');
    }
    return ServewrightView.fromJson(jsonDecode(response.body) as Map<String, dynamic>);
  }
}
