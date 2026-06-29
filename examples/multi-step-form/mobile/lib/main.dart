import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

import 'package:servewright_flutter/servewright_flutter.dart';
import 'package:servewright_flutter_material/servewright_flutter_material.dart';

void main() {
  runApp(const HelloApp());
}

class HelloApp extends StatefulWidget {
  const HelloApp({super.key});

  @override
  State<HelloApp> createState() => _HelloAppState();
}

class _HelloAppState extends State<HelloApp> {
  static const endpoint = String.fromEnvironment(
    'SERVEWRIGHT_URL',
    defaultValue: 'http://localhost:8080/servewright/view/hello',
  );

  late final Registry _registry;
  late final Renderer _renderer;
  Widget? _content;
  String? _error;

  @override
  void initState() {
    super.initState();
    _registry = createRegistry();
    registerMaterialPrimitives(_registry);
    _renderer = createRenderer(_registry);
    _loadView();
  }

  Future<void> _loadView() async {
    try {
      final response = await http.get(Uri.parse(endpoint));
      if (response.statusCode != 200) {
        throw Exception('HTTP ${response.statusCode}');
      }
      final view = ServewrightView.fromJson(jsonDecode(response.body) as Map<String, dynamic>);
      setState(() {
        _content = _renderer.render(view);
        _error = null;
      });
    } catch (error) {
      setState(() {
        _error = error.toString();
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(title: const Text('Servewright Hello')),
        body: Center(
          child: _error != null
              ? Text('Failed to load view: $_error')
              : _content ?? const CircularProgressIndicator(),
        ),
      ),
    );
  }
}
