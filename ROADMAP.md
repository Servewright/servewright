# Servewright — Roadmap

Frontière V1 / V2 / V3. Règle inviolable : **phase N+1 interdite tant que phase N n'est pas finie et montrable.**

---

## V1 — Le noyau qui rend la thèse indéniable

Thèse à prouver : *un seul contrat sémantique, piloté serveur, rendu nativement en deux clients avec le design system de leur choix.*

Tout ce qui ne sert pas directement cette phrase est hors V1.

### Livrables (5, tous polis simultanément)
- **`servewright-core`** (Java pur, zéro dépendance framework) : modèle de primitives, registry serveur, validation, sérialisation, machine à états, génération de l'arbre UI.
- **`servewright-spring-boot-starter`** : auto-config + endpoint SSE + customizers.
- **Le contrat** (JSON Schema) + chaîne de codegen → Dart / TS.
- **`servewright-react`** (moteur, sans design system) + **`servewright-react-shadcn`** (un seul adaptateur).
- **`servewright-flutter`** (moteur) + **`servewright-flutter-material`** (un seul adaptateur).

Transverses V1 :
- **Builder de primitive typé** (Java + TS) → primitives custom locales possibles dès la V1 (cœur de différenciation).
- **~8-10 primitives standard**, composition incluse dans le contrat.
- Les **5 points structurants** intégrés au contrat (actions/binding, validation, fallback version, resync, a11y — cf. ARCHITECTURE §4).

### Exemple V1 (preuve technique, minimal)
- **Formulaire multi-étapes** piloté serveur, rendu identique React + Flutter, transitions poussées en SSE.
- Reste minimal : c'est une preuve de fonctionnement, pas la vitrine.

### Non négociable
- **React ET Flutter tous les deux.** Un seul renderer = une lib SDUI de plus parmi cent. Sous pression, couper des primitives (descendre à 6-8), **jamais** un renderer.

---

## V2 — Amplification (une fois le noyau solide et montrable)

### La vitrine
- **Site vitrine en Next.js classique** (beau, rapide, SEO) — *pas* en SDUI.
- **Dashboard démo « fake »** entièrement rendu par Servewright (terrain naturel du SDUI : data-driven, interactif, stateful ; « fake » = état serveur hardcodé crédible, ni données réelles ni auth).
- Trio différenciateur de la démo (= l'argument du projet, rendu manipulable) :
  1. **Toggle React ↔ Flutter** côte à côte (Flutter Web nu, pas de frame téléphone factice).
  2. **Switch design system live à JSON constant** (shadcn → MUI sans que le JSON change).
  3. **Panneau JSON du contrat** visible à côté du rendu.
- Dashboard cadré à **un seul objectif de preuve**. Tout écran décoratif sort.

### Preuve native mobile (low-cost d'abord)
- **Flutter Web** dans la démo = preuve cross-client, zéro friction. (priorité)
- **Vidéo d'app native réelle** changeant live + **APK Android en download direct** = preuve native universelle.
- **TestFlight** seulement si le public cible veut manipuler l'app iOS lui-même (99 $/an + admin + friction d'install non justifiés sinon).
- **Pas de store public** sans raison business.

### Outillage & robustesse
- Adaptateurs supplémentaires : `servewright-react-mui`, `servewright-react-tailwind`, `servewright-flutter-cupertino`.
- ETag / Cache-Control sur l'état initial via le starter ; hooks pour le cache de l'implémenteur.
- **Benchmark** sur le dashboard démo (premières vraies données → optimisation pilotée par la mesure).
- Doc contributeur sérieuse : CONTRIBUTING, ADR, primitive-template, suite de conformité, partition CI.
- Threat model sécurité documenté.

---

## V3 — La vision (une fois qu'il y a quelque chose à montrer)

- **`servewright-mcp`** : génération de primitives / écrans depuis Figma, wireframe ou description, via l'**API publique** de Servewright. Package séparé, ne touche pas le core. Ton edge IA, en second artefact portfolio qui pointe vers le premier.
- Primitives composites avancées, navigation multi-écrans, actions chaînées (territoire « app-builder » — **émerge de la composition**, pas codé frontalement).
- **Bindings serveur autres langages** (Kotlin / Go / TS) — prouve l'extensibilité du protocole. Porté par la qualité de la spec + un guide « how to write a server binding », pas par du code multi-langage livré tôt.
- États loading/error standardisés, i18n, GOVERNANCE.md.

---

## Discipline de scope (rappel)

Le risque n'est pas la qualité des idées, c'est le **nombre de fronts ouverts en parallèle**. Contexte solo + charge perso = une seule règle tient le projet en vie :

> Une V1 **finie et déployée** vaut dix fois une vision à 30 % étalée sur trois phases.

Le MCP, le dashboard, le multi-adaptateur, le mobile natif sont des **récompenses de fin de phase**, pas des chantiers d'ouverture.