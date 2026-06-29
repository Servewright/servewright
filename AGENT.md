# AGENT.md — Règles permanentes pour Cursor (Servewright)

Tu construis Servewright, une lib de Server-Driven UI. Lis `ARCHITECTURE.md`, `ROADMAP.md` et `SCAFFOLDING.md` avant toute action. Ces règles s'appliquent à CHAQUE phase, sans exception.

## Règle de gate (la plus importante)

Tu travailles UNE phase à la fois, dans l'ordre. Tu ne commences JAMAIS une phase tant que la précédente n'a pas passé sa gate de sortie.

À la fin de chaque phase, tu produis un **rapport de gate** qui prouve, point par point, que chaque critère de sortie est rempli (commande exécutée + sortie). Si UN critère échoue, tu corriges avant de t'arrêter. Tu ne déclares JAMAIS une phase terminée sans que tous ses tests passent réellement (exécutés, pas supposés).

Tu n'anticipes pas les phases futures : pas de primitive en avance, pas de binding avant la phase actions, pas de SSE avant la phase transitions. Le scope de la phase courante est une limite stricte.

## Règle sémantique (non négociable)

Le protocole est 100% sémantique. Une primitive décrit CE QUE c'est et CE QU'elle fait, JAMAIS à quoi elle ressemble. Aucune classe CSS, couleur, ou détail de style ne transite dans le contrat ni le core. Si tu te surprends à mettre du style dans une primitive ou le core, arrête : c'est une erreur d'architecture.

## Règle de frontière core/framework

`servewright-core` (Java) : zéro dépendance framework. Doit compiler et tourner SANS Spring sur le classpath. Si un mécanisme exige du scan/DI, il va dans `servewright-spring-boot-starter`, jamais dans le core. Vérifie cette propriété à chaque ajout au core.

## Règle de symétrie client

Les renderers React (`@servewright/react`) et Flutter (`servewright_flutter`) doivent rester structurellement identiques : même notion de registry, même `register(type, component)` avec last-write-wins, même fallback "primitive inconnue" (placeholder, jamais crash). Un contributeur qui connaît un côté doit comprendre l'autre.

## Règles de test (chaque phase)

- **Tests unitaires** : chaque unité de logique (sérialisation, registry, validation, routing) a ses tests.
- **Tests d'intégration** : la chaîne de bout en bout de la phase est testée (ex. endpoint → JSON conforme au schéma ; client → rendu attendu).
- **Validation de schéma** : tout JSON produit/consommé est validé contre les JSON Schema de `spec/`. Un test échoue si le JSON dévie du contrat.
- Les tests doivent être exécutables par une seule commande par écosystème (`mvn verify`, `pnpm test`, `dart test`). Tu donnes ces commandes dans le rapport de gate.

## Règles d'outillage

- Java 21, Maven multi-module sous `java/`.
- pnpm workspaces sous `js/`, TypeScript strict.
- Flutter/Dart sous `dart/`.
- Pas de dépendance lourde non justifiée. Tu listes toute nouvelle dépendance et sa raison dans le rapport de gate.
- Commits atomiques par sous-étape, messages conventionnels (`feat:`, `test:`, `chore:`).

## Format du rapport de gate (à produire en fin de phase)

```
## Gate Phase N — rapport
- [ ] Critère 1 : <commande> → <résultat observé>
- [ ] Critère 2 : ...
Tests unitaires : <commande> → X passed
Tests intégration : <commande> → X passed
Validation schéma : <commande> → OK
Nouvelles dépendances : <liste + justification, ou "aucune">
Limites respectées (rien d'une phase future ajouté) : oui/non
VERDICT : GATE PASSÉE / NON PASSÉE
```

Si VERDICT = NON PASSÉE, tu continues à corriger. Tu ne passes pas la main tant que ce n'est pas PASSÉE.