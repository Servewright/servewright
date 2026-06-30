import 'dart:async';
import 'dart:convert';

import 'package:http/http.dart' as http;
import 'package:servewright_flutter/src/transition.dart';

abstract class ServewrightTransport {
  StreamSubscription<String>? connect(
    String screen, {
    required void Function(ServewrightTransition transition) onTransition,
    void Function(Object error)? onError,
  });
}

class SseTransport implements ServewrightTransport {
  SseTransport({this.streamUrl = '/servewright/stream', http.Client? client})
      : _client = client ?? http.Client();

  final String streamUrl;
  final http.Client _client;

  @override
  StreamSubscription<String>? connect(
    String screen, {
    required void Function(ServewrightTransition transition) onTransition,
    void Function(Object error)? onError,
  }) {
    final controller = StreamController<String>();
    final request = http.Request('GET', Uri.parse('$streamUrl/$screen'));
    request.headers['Accept'] = 'text/event-stream';

    _client.send(request).then((response) {
      response.stream
          .transform(utf8.decoder)
          .transform(const LineSplitter())
          .listen(
            controller.add,
            onError: onError,
          );
    }).catchError(onError ?? (_) {});

    String? dataBuffer;
    return controller.stream.listen((line) {
      if (line.startsWith('data:')) {
        dataBuffer = line.substring(5).trim();
      } else if (line.isEmpty && dataBuffer != null) {
        final transition = ServewrightTransition.fromJson(
          jsonDecode(dataBuffer!) as Map<String, dynamic>,
        );
        onTransition(transition);
        dataBuffer = null;
      }
    }, onError: onError);
  }
}

class ImmediateTransport implements ServewrightTransport {
  ImmediateTransport(this.transitions);

  final List<ServewrightTransition> transitions;

  @override
  StreamSubscription<String>? connect(
    String screen, {
    required void Function(ServewrightTransition transition) onTransition,
    void Function(Object error)? onError,
  }) {
    for (final transition in transitions) {
      onTransition(transition);
    }
    return null;
  }
}
