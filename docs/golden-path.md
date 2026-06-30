# Golden path — add a primitive

This is the end-to-end checklist for adding a **standard primitive** to Servewright V1.

## 1. Contract (`spec/`)

Create `spec/primitives/<kebab-name>.schema.json` describing props and required fields. Keep it **semantic** — no styling.

Register the type in any composite schemas if children are allowed.

## 2. Server (`servewright-core`)

1. Add a factory on `StandardNodes` (e.g. `StandardNodes.banner(...)`).
2. Add server-side validation if props need cross-field rules.
3. Add a unit test for serialization round-trip.

No Spring annotations in core.

## 3. Spring starter (if needed)

Only if the primitive needs HTTP-specific wiring. Most primitives need no starter changes.

## 4. React renderer

1. Implement `PrimitiveComponent` in `@servewright/react-shadcn` (or your design-system package).
2. Register with `registry.register("Banner", createBannerComponent())`.
3. Map accessibility: labels, roles, `aria-*` where applicable.
4. Default missing props gracefully (`?? ""`, `?? false`).

## 5. Flutter renderer

1. Implement `Widget buildBannerPrimitive(ServewrightNode node, RenderContext ctx)` in `servewright_flutter_material`.
2. Register in `registerMaterialPrimitives`.
3. Wrap interactive widgets with `Semantics` (label, button, textField).
4. Default missing props gracefully.

## 6. Conformance

Add `conformance/cases/<kebab-name>.json`:

```json
{
  "primitive": "Banner",
  "root": {
    "id": "hero",
    "type": "Banner",
    "props": { "title": "Welcome" }
  },
  "expect": { "contains": ["Welcome"] }
}
```

Both `pnpm --filter @servewright/conformance test` and `flutter test test/conformance_test.dart` must pass.

## 7. Verify

```bash
mvn -f java/pom.xml verify
pnpm -r build && pnpm -r test
cd dart/servewright_flutter && flutter test
```

## Reference primitive

Use **Text** as the minimal template (`spec/primitives/text.schema.json`, `StandardNodes.text`, shadcn `createTextComponent`, material `buildTextPrimitive`, `conformance/cases/text.json`).

For interactive fields, use **TextInput** (validation, errors, a11y, binding).

For transitions touching the primitive, add a `transition-*.json` case under `conformance/cases/`.
