# Servewright

> Server-Driven UI. Le serveur écrit la pièce, le client la joue.

Servewright est une librairie open source de Server-Driven UI : un **contrat sémantique unique**, piloté par le backend, rendu nativement sur plusieurs clients (React, Flutter) avec le **design system de leur choix**.

Le serveur décrit *ce que* l'UI est et *ce qu'elle fait* — jamais à quoi elle ressemble. Les clients mappent les primitives sémantiques vers leurs propres composants. Changez l'UI côté serveur ; les clients suivent, sans redéploiement.

## Statut

🚧 **En conception.** L'architecture est figée, le code démarre.

- [`ARCHITECTURE.md`](./ARCHITECTURE.md) — décisions de conception et règle d'or sémantique.
- [`ROADMAP.md`](./ROADMAP.md) — périmètre V1 / V2 / V3 et discipline de scope.
- [`BRANDING.md`](./BRANDING.md) — identité visuelle et design system.

## Principe en une image

```
GET /servewright/view/{screen}   →  arbre UI sémantique (JSON)
[interaction client]             →  POST /servewright/action  (intention nommée)
[machine à états serveur]        →  SSE: transition (patch ciblé par id)
[client]                         →  applique le patch, rend avec son design system
```

## Écosystèmes

| Package | Registre |
|---|---|
| `io.servewright:servewright-core` | Maven Central |
| `io.servewright:servewright-spring-boot-starter` | Maven Central |
| `@servewright/react` | npm |
| `@servewright/react-shadcn` | npm |
| `servewright_flutter` | pub.dev |
| `servewright_flutter_material` | pub.dev |

## Licence

Apache-2.0.