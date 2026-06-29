# PHASE 4 — Transitions, diff/patch & transport SSE

## Objectif
Passer du "recharge le View complet" à la propagation incrémentale : le serveur pousse des transitions (patchs ciblés par id) via SSE, le client réconcilie sans perdre l'état local. Implémenter le resync.

## Modèle à implémenter (rappel ARCHITECTURE.md)
- **Transition** : `{ basedOn, stateVersion, patches[] }`. Patches adressés par node `id` (PAS par JSON Pointer positionnel). Ops V1 : `replace`, `insert`, `remove`, `setError`, `setLoading`.
- **Transport abstrait** : interface `Transport`, impl SSE par défaut dans le starter. Le composant client ne connaît pas le transport.
- **Resync** : `stateVersion` monotone. Si `basedOn` ≠ version client → le client redemande un View complet.
- **Réconciliation** : application des patchs par id ; l'état local d'un champ en cours d'édition NON visé par le patch est préservé ; si un patch vise un champ en édition, l'état local non soumis prime (règle à écrire et tester).
- **Sérialisation abstraite** : JSON par défaut, interface remplaçable.

## Livrables
- Core : `Transition`, `TransitionBuilder` (`.replace/.insert/.remove/.setError/.setLoading`), validation que chaque `target` d'op existe dans l'état courant (erreur au build sinon), gestion `stateVersion`/`basedOn`.
- Starter : endpoint SSE, push des transitions, abstraction `Transport`, ETag/Cache-Control sur le View initial.
- Renderers (x2) : application des patchs par id, réconciliation préservant l'état local, détection de désync (basedOn) → refetch.

## Tests exigés
- **Unitaires core** : TransitionBuilder rejette une op visant un id inexistant ; séquence de patchs produit l'arbre attendu ; stateVersion s'incrémente correctement.
- **Intégration** : flux complet action → transition SSE → client à jour. Désync simulée (basedOn périmé) → client refetch.
- **Renderers (x2)** : patch `replace` met à jour le bon noeud ; `setError` attache l'erreur au bon champ ; un champ en cours d'édition non visé n'est PAS écrasé ; reconnexion SSE → resync sans perte de saisie.
- **Conformance** : cas "même séquence de patchs → même arbre final" sur les 2 renderers.

## Critères de sortie (gate)
- [ ] Tests Phases 0–3 toujours verts.
- [ ] Flux action→transition SSE→rendu incrémental prouvé end-to-end, React ET Flutter.
- [ ] Patchs adressés par id, ops V1 implémentées et testées.
- [ ] TransitionBuilder valide l'existence des targets au build (test de l'échec).
- [ ] Resync prouvé : désync détectée → refetch, sans perte de l'état local non soumis.
- [ ] Réconciliation : règle "état local non soumis prime" testée explicitement.
- [ ] Transport et sérialisation derrière des interfaces remplaçables.

## Hors scope (interdit en Phase 4)
La perf/benchmark/cache de données (mesure réelle = plus tard, sur le dashboard démo). Les adaptateurs design system supplémentaires. Le MCP. Le site.
