# PHASE 3 — Actions & data-binding

## Objectif
Rendre l'UI interactive. Implémenter le modèle de binding décidé : état de saisie LOCAL au client, remontée selon une politique de trigger déclarée par primitive, actions sémantiques nommées routées par le serveur, validation des deux côtés.

ATTENTION : c'est la phase la plus délicate. Respecte exactement les décisions d'ARCHITECTURE.md §4 et le modèle ci-dessous.

## Modèle à implémenter (rappel)
- **État local** : le client tient la valeur des champs ; le serveur tient la structure et l'état validé.
- **Trigger par primitive** : `onSubmit` | `onBlur` | `onChange` (+ `debounceMs`). Défaut `onBlur`. Transition automatique onBlur→onChange côté client une fois un champ invalide (comportement client, pas dans le contrat).
- **Action sémantique** : `{ type, target, payload, stateVersion }` envoyée à un canal unique `POST /servewright/action`. Jamais d'URL métier côté client.
- **Target = le groupe/Form** : le payload est borné au groupe (permet validation globale au submit).
- **Routing serveur** : `ActionRouter` programmatique dans le core (`on(type, target, handler)`) + sucre Spring `@ServewrightController` / `@OnAction` / `@Payload` dans le starter. Collision de handlers = erreur au boot. Log des routes au démarrage.
- **Validation** : règles déclarées dans la primitive, exécutées client (UX) ET re-vérifiées serveur (confiance). Erreurs renvoyées attachées au noeud par id.
- **Validation async** : opt-in via `@OnAsyncValidation`, avec état `validating`.

## Livrables
- Core : `Action`, `ActionRouter`, `ActionHandler` (interface fonctionnelle), modèle de validation déclarative.
- Starter : `@ServewrightController`, `@OnAction(type, target)`, `@Payload` (binding payload typé), `@OnAsyncValidation`, endpoint `POST /servewright/action`, log des routes, erreur de collision au boot.
- Renderers (x2) : gestion de l'état local des champs, application des triggers, dispatch des actions, affichage des erreurs par id, état `validating`.

## Tests exigés
- **Unitaires core** : routing (type+target → bon handler) ; collision détectée au boot ; validation déclarative (cas valides/invalides).
- **Intégration** : `POST /servewright/action` avec payload de groupe → handler exécuté → réponse cohérente ; re-validation serveur rejette un payload invalide même si le client l'aurait accepté.
- **Renderers (x2)** : saisie met à jour l'état local sans réseau ; trigger onBlur déclenche au bon moment ; champ invalide passe en onChange ; erreur serveur s'affiche sous le bon champ ; payload envoyé borné au groupe.
- **Binding payload typé (starter)** : `@Payload Dto` désérialise correctement ; payload malformé → erreur propre.

## Critères de sortie (gate)
- [ ] Tests Phases 0–2 toujours verts.
- [ ] Un formulaire (Form > champs > Button submit) fonctionne end-to-end : saisie locale, submit, validation serveur, erreurs affichées. Prouvé en démo React ET Flutter.
- [ ] ActionRouter programmatique testé ; sucre `@OnAction` testé ; collision au boot testée.
- [ ] Re-validation serveur prouvée (un payload invalide passe le client mais est rejeté serveur).
- [ ] Validation async opt-in fonctionne avec état `validating` (au moins un champ de démo).
- [ ] Symétrie React/Flutter du binding respectée.
- [ ] Aucune transition/diff/SSE encore (la réponse d'action peut renvoyer un View complet pour l'instant).

## Hors scope (interdit en Phase 3)
Les transitions incrémentales / diff / patch / SSE (Phase 4). En Phase 3, une action peut répondre par un View complet rechargé — l'optimisation par patch vient après.
