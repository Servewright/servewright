# PHASE 0 — Fondations du monorepo

## Objectif
Mettre en place l'ossature multi-écosystème vide mais buildable, et la CI partitionnée. Aucune logique métier ici — juste que chaque écosystème compile et que `hello world test` passe dans chacun.

## Livrables
- Arborescence de `SCAFFOLDING.md` créée : `java/`, `js/`, `dart/`, `examples/`, `conformance/`, `spec/`, `.github/workflows/`.
- `java/pom.xml` (parent Maven) + deux modules vides qui compilent : `servewright-core`, `servewright-spring-boot-starter`.
- `js/` workspace pnpm + deux packages vides qui build (TS strict) : `servewright-react`, `servewright-react-shadcn`.
- `dart/` avec deux packages : `servewright_flutter`, `servewright_flutter_material`.
- 4 workflows CI déclenchés par chemin : `java.yml`, `js.yml`, `dart.yml`, `conformance.yml` (conformance peut être un stub no-op pour l'instant).
- Un test trivial par écosystème (ex. `assertEquals(1+1, 2)`) pour prouver que le pipeline de test tourne.

## Tests exigés
- Java : `mvn -f java/pom.xml verify` compile les 2 modules et exécute le test trivial.
- JS : `pnpm -C js -r build` build les 2 packages ; `pnpm -C js -r test` exécute le test trivial.
- Dart : `dart test` (ou `flutter test`) dans chaque package dart passe.

## Critères de sortie (gate)
- [ ] L'arborescence complète existe et correspond à SCAFFOLDING.md.
- [ ] `mvn -f java/pom.xml verify` → BUILD SUCCESS, test trivial vert.
- [ ] `pnpm -C js -r build` → succès ; `pnpm -C js -r test` → vert.
- [ ] Tests Dart → verts dans les 2 packages.
- [ ] Les 4 workflows existent et sont valides (syntaxe YAML correcte, paths configurés).
- [ ] `servewright-core` ne déclare AUCUNE dépendance framework (vérifier le pom).
- [ ] Aucune logique métier ajoutée (pas de primitive, pas de renderer réel).

## Hors scope (interdit en Phase 0)
Toute primitive, tout renderer fonctionnel, tout endpoint. Uniquement l'ossature et la preuve que ça build/teste.