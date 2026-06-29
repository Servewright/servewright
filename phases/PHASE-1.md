# PHASE 1 — Vertical slice : la primitive Text de bout en bout

## Objectif
Prouver que toute la chaîne tient sur UNE primitive. Le serveur émet un `Text`, sérialisé selon le contrat, rendu en React ET en Flutter. C'est le jalon fondateur : tant qu'il n'est pas vert, rien d'autre.

## Livrables
- `servewright-core` : `Node`, `View`, factory `Node.text(...)`, sérialisation JSON (via une abstraction `Serializer`, impl Jackson dans le starter — le core reste sans Spring). Voir le squelette `slice-examples/core-Text.java`.
- `servewright-spring-boot-starter` : auto-config + endpoint `GET /servewright/view/{screen}` qui renvoie un `View` JSON conforme à `spec/protocol/view.schema.json`.
- `servewright-react` : moteur `createRenderer()` (registry + renderer récursif + fallback inconnu). Voir `slice-examples/react-Text.tsx`.
- `servewright-react-shadcn` : adaptateur qui enregistre le composant `Text` (mappe emphasis → balise/composant).
- `servewright_flutter` + `servewright_flutter_material` : symétrique exact. Voir `slice-examples/flutter-Text.dart`.
- `examples/multi-step-form` réduit à un écran "hello" : serveur Spring + appli React + appli Flutter consommant le même endpoint.

## Tests exigés
- **Unitaires core** : `View.of("hello", Node.text("greeting","Bonjour"))` se sérialise en JSON exact attendu ; round-trip si désérialisation.
- **Validation schéma** : le JSON produit valide contre `spec/protocol/view.schema.json` ET `spec/primitives/text.schema.json` (test qui échoue si déviation).
- **Intégration serveur** : appel HTTP `GET /servewright/view/hello` → 200 + corps JSON conforme.
- **Unitaires renderer React** : `render(view)` produit le bon élément ; un type inconnu produit le placeholder, pas une exception.
- **Unitaires renderer Flutter** : widget test, `Text` rendu ; type inconnu → SizedBox, pas de crash.

## Critères de sortie (gate)
- [ ] Tous les tests Phase 0 passent toujours (non-régression).
- [ ] Core : tests de sérialisation + validation schéma verts. `servewright-core` toujours sans dépendance framework.
- [ ] Starter : test d'intégration de l'endpoint vert, corps conforme au schéma.
- [ ] React : test unitaire rendu + test fallback verts.
- [ ] Flutter : widget test rendu + test fallback verts.
- [ ] DÉMO MANUELLE prouvée : lancer le serveur, lancer le client React et le client Flutter, "Bonjour" s'affiche dans les DEUX depuis le même endpoint. (Capture/log dans le rapport.)
- [ ] Symétrie React/Flutter respectée (même API de registry).
- [ ] Aucune autre primitive que Text. Aucun binding, aucune action, aucun SSE.

## Hors scope (interdit en Phase 1)
Les 9 autres primitives, le data-binding, les actions, les transitions, le SSE, la composition (children). Uniquement Text, lecture seule, statique.