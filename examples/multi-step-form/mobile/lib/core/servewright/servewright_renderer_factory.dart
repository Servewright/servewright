import 'package:servewright_flutter/servewright_flutter.dart';
import 'package:servewright_flutter_material/servewright_flutter_material.dart';

class ServewrightRendererFactory {
  static Renderer create() {
    final registry = createRegistry();
    registerMaterialPrimitives(registry);
    return createRenderer(registry);
  }
}
