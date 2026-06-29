# PHASE 2 — Le set de primitives + composition

## Objectif
Étendre du slice mono-primitive au vocabulaire complet V1, y compris la composition (arbres imbriqués). Toujours lecture seule / statique — aucune interactivité encore.

## Livrables
Les ~10 primitives V1, définies dans `spec/` (un JSON Schema chacune), implémentées dans le core (factory/builder), et rendues dans les DEUX renderers via les adaptateurs :
- Structure : `Container`, `Form`, `Group` (composables, portent `children`).
- Saisie (rendu seulement, PAS de binding actif en Phase 2) : `TextInput`, `Select`, `Checkbox`, `Button`.
- Affichage : `Text` (déjà fait), `Stat`, `Table` (lecture seule).
- Le builder de primitive typé côté core (`Primitive.define(...).prop(...).build()`) + la `PrimitiveRegistry` avec `withStandardPrimitives()` et validation d'unicité au boot.
- Rendu récursif de la composition (un `Container` contenant des enfants) dans React et Flutter.

## Tests exigés
- **Unitaires core** : chaque primitive se définit et se sérialise correctement ; registry rejette les noms en double AU BOOT (test qui attend l'exception au démarrage).
- **Validation schéma** : un arbre composite (Form > Group > TextInput + Button) valide contre les schémas. Un arbre avec prop invalide échoue.
- **Intégration** : endpoint renvoyant un arbre composite complet, conforme.
- **Renderers (x2)** : chaque primitive rend le bon composant ; un arbre imbriqué rend la bonne hiérarchie ; fallback inconnu intact.
- **Conformance (début)** : premiers cas dans `conformance/cases/` — un JSON d'entrée + le rendu attendu, exécutés contre les 2 renderers.

## Critères de sortie (gate)
- [ ] Tests Phases 0 et 1 toujours verts.
- [ ] Les 10 primitives définies dans `spec/`, chacune avec son schéma.
- [ ] Core : builder + registry + validation unicité au boot, tous tests verts.
- [ ] Composition récursive rendue correctement dans React ET Flutter (test d'arbre imbriqué).
- [ ] Validation schéma : arbre composite conforme ; cas négatifs (prop invalide) échouent comme attendu.
- [ ] Suite de conformance amorcée avec au moins 1 cas par primitive, verte sur les 2 renderers.
- [ ] Primitives de saisie présentes mais INERTES (rendues, sans remontée d'état). Aucune action/binding.

## Hors scope (interdit en Phase 2)
Le data-binding, les actions, la validation à l'exécution, les transitions, le SSE. Les champs de saisie s'affichent mais ne font rien encore.
