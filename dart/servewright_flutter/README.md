# servewright_flutter

Servewright **renderer engine** for Flutter. Registry, recursive renderer, binding, transitions, and transport — **no Material/Cupertino widgets included**.

## Install

```yaml
dependencies:
  servewright_flutter: ^0.1.0
```

## Usage

```dart
import 'package:servewright_flutter/servewright_flutter.dart';
import 'package:servewright_flutter_material/servewright_flutter_material.dart';

final registry = createRegistry();
registerMaterialPrimitives(registry);
```

See `servewright_flutter_material` for the reference Material adapter.

## License

Apache-2.0
