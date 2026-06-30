# Contributing to Servewright

Thank you for contributing. Servewright is a **semantic** Server-Driven UI library: the protocol describes *what* UI is, never *how* it looks.

Read [`ARCHITECTURE.md`](./ARCHITECTURE.md) and [`AGENT.md`](./AGENT.md) before opening a PR. One phase at a time; do not mix V2 scope into V1 fixes.

## Development setup

| Ecosystem | Requirements | Verify |
|-----------|--------------|--------|
| Java | JDK 21, Maven | `mvn -f java/pom.xml verify` |
| JavaScript | Node 20+, pnpm | `pnpm -r build && pnpm -r test` |
| Flutter | Flutter 3.27+, Dart 3.6+ | `cd dart/servewright_flutter && flutter test` |
| Conformance | pnpm | `pnpm --filter @servewright/conformance test` |

Run the demo (requires Java server on `:8080`):

```bash
mvn -f java/pom.xml -pl servewright-spring-boot-starter spring-boot:run
# React: pnpm --filter multi-step-form-web dev
# Flutter: cd examples/multi-step-form/mobile && flutter run
```

## Golden path: add a primitive end-to-end

Follow [`docs/golden-path.md`](./docs/golden-path.md). Summary:

1. **JSON Schema** — `spec/primitives/<name>.schema.json`
2. **Core** — `StandardNodes.<name>()` factory + validation in `servewright-core`
3. **React adapter** — register in `@servewright/react-shadcn` (or your DS package)
4. **Flutter adapter** — register in `servewright_flutter_material`
5. **Conformance** — `conformance/cases/<name>.json` (React + Flutter CI)
6. **Tests** — unit tests for validation/serialization; integration if server-facing

Use [`docs/primitive-template.md`](./docs/primitive-template.md) as a checklist.

## Pull request checklist

- [ ] Semantic-only contract (no CSS, colors, or presentation in `spec/` or core)
- [ ] `servewright-core` compiles without Spring on the classpath
- [ ] React and Flutter renderers stay symmetric (registry, fallback, last-write-wins)
- [ ] Conformance case added or updated for behavioral changes
- [ ] `mvn verify`, `pnpm -r test`, `flutter test` pass locally
- [ ] ADR added if the change is architectural (see `docs/adr/`)

## Commit messages

Use [Conventional Commits](https://www.conventionalcommits.org/): `feat:`, `fix:`, `test:`, `docs:`, `chore:`.

## Security

See [`docs/threat-model.md`](./docs/threat-model.md). Never merge server-driven HTML injection vectors into primitives without explicit sanitization and authorization review.
