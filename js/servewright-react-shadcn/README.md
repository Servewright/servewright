# @servewright/react-shadcn

Reference **design-system adapter** for Servewright on React (minimal semantic HTML mapping). Peer-depends on `@servewright/react`.

## Install

```bash
npm install @servewright/react @servewright/react-shadcn react
```

## Usage

```tsx
import { createRegistry } from "@servewright/react";
import { registerShadcnPrimitives } from "@servewright/react-shadcn";

const registry = createRegistry();
registerShadcnPrimitives(registry);
```

Override any primitive: `registry.register("Button", myButton)`.

## License

Apache-2.0
