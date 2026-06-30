# Primitive template checklist

Copy this when adding a new primitive named `<Name>`.

## Files to create or update

| Step | Location | Action |
|------|----------|--------|
| Schema | `spec/primitives/<name>.schema.json` | Define props JSON Schema |
| Core factory | `java/.../StandardNodes.java` | `static Node <name>(...)` |
| Core test | `java/.../ViewSerializationTest` or dedicated test | Round-trip JSON |
| React | `js/servewright-react-shadcn/src/primitives.tsx` | Component + `register` |
| Flutter | `dart/servewright_flutter_material/lib/primitives.dart` | Widget + `register` |
| Conformance | `conformance/cases/<name>.json` | `contains` expectations |
| A11y (if interactive) | conformance `attributes` / `semanticsLabels` | Label or role checks |

## Semantic rules

- Props describe behavior and content only.
- Every interactive control needs a **label** prop or accessible name.
- Unknown/missing props must not crash renderers.
- Node `id` is stable across transitions.

## Transition ops (if mutable)

If the primitive can be patched:

| Op | Typical use |
|----|-------------|
| `replace` | Swap entire node (e.g. success state) |
| `insert` / `remove` | Dynamic lists |
| `setError` | Validation feedback on fields |
| `setLoading` | Async validation / submit spinner |

Add a `conformance/cases/transition-<scenario>.json` per op you support.

## Done when

- [ ] Schema validates example JSON
- [ ] Core builds without Spring
- [ ] React + Flutter conformance green
- [ ] Fallback case if primitive can be unknown on older clients (N/A for new standard types)
- [ ] ADR not required for straightforward primitives; required for protocol changes
