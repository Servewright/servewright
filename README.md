# Servewright

> Server-Driven UI. Le serveur écrit la pièce, le client la joue.

Servewright is an open-source **Server-Driven UI** library: one **semantic contract**, driven by the backend, rendered natively on React and Flutter with **your design system**.

The server describes *what* the UI is and *what it does* — never how it looks. Clients map semantic primitives to their own components. Change UI on the server; clients follow without redeployment.

## Status

**V1 complete** — primitives, actions, validation, transitions (SSE), conformance suite, publishable packages.

| Doc | Purpose |
|-----|---------|
| [`ARCHITECTURE.md`](./ARCHITECTURE.md) | Design decisions |
| [`ROADMAP.md`](./ROADMAP.md) | V1 / V2 / V3 scope |
| [`CONTRIBUTING.md`](./CONTRIBUTING.md) | Contributor guide |
| [`docs/golden-path.md`](./docs/golden-path.md) | Add a primitive end-to-end |
| [`docs/threat-model.md`](./docs/threat-model.md) | Security considerations |

## Quick start

```bash
# Verify all ecosystems
mvn -f java/pom.xml verify
pnpm -r build && pnpm -r test
cd dart/servewright_flutter && flutter test

# Demo (Java server + React or Flutter client)
mvn -f java/pom.xml -pl servewright-spring-boot-starter spring-boot:run
```

## Protocol flow

```
GET /servewright/view/{screen}   →  semantic UI tree (JSON)
POST /servewright/action         →  named intent + payload
GET /servewright/stream/{screen} →  SSE transitions (patches by node id)
```

## Packages

| Package | Registry |
|---------|----------|
| `io.servewright:servewright-core` | Maven |
| `io.servewright:servewright-spring-boot-starter` | Maven |
| `@servewright/react` | npm |
| `@servewright/react-shadcn` | npm |
| `servewright_flutter` | pub.dev |
| `servewright_flutter_material` | pub.dev |

## License

Apache-2.0
