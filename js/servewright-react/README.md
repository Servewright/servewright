# @servewright/react

Servewright **renderer engine** for React. Registry, recursive renderer, binding context, transitions, and transport abstractions — **no design system included**.

## Install

```bash
npm install @servewright/react react
```

## Usage

```tsx
import { ServewrightView, createRegistry } from "@servewright/react";
import { registerShadcnPrimitives } from "@servewright/react-shadcn";

const registry = createRegistry();
registerShadcnPrimitives(registry);

<ServewrightView view={view} registry={registry} />
```

## API

- `createRegistry()` / `createRenderer()` — primitive registration (last-write-wins)
- `ServewrightView` — binding, actions, SSE transport, transition apply + resync
- `applyTransition`, `SseTransport`, `ImmediateTransport`

## License

Apache-2.0
