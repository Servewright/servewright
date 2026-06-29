# Servewright — Scaffolding du monorepo

Plan d'arborescence, packaging par écosystème, partition CI, et le premier vertical slice. À poser dans `servewright/servewright`.

---

## 1. Arborescence

```
servewright/
├── ARCHITECTURE.md
├── ROADMAP.md
├── BRANDING.md
├── README.md
├── CONTRIBUTING.md
├── LICENSE                          (Apache-2.0)
├── .gitignore
│
├── spec/                            # le contrat — source de vérité, langage-neutre
│   ├── primitives/                  # un JSON Schema par primitive
│   │   └── text.schema.json
│   ├── protocol/                    # View, Action, Transition
│   │   ├── view.schema.json
│   │   ├── action.schema.json
│   │   └── transition.schema.json
│   └── codegen/                     # scripts de génération → Dart / TS / Java
│
├── java/                            # tout l'écosystème JVM (Maven multi-module)
│   ├── pom.xml                      # parent
│   ├── servewright-core/            # Java pur, zéro dépendance framework
│   └── servewright-spring-boot-starter/
│
├── js/                              # tout l'écosystème npm (pnpm workspace)
│   ├── package.json                 # workspace root
│   ├── pnpm-workspace.yaml
│   ├── servewright-react/           # moteur (registry + renderer + types générés)
│   └── servewright-react-shadcn/    # adaptateur V1
│
├── dart/                            # tout l'écosystème pub.dev
│   ├── servewright_flutter/         # moteur
│   └── servewright_flutter_material/# adaptateur V1
│
├── examples/
│   └── multi-step-form/             # exemple V1 : serveur + React + Flutter
│       ├── server/                  # appli Spring qui utilise le starter
│       ├── web/                     # appli React
│       └── mobile/                  # appli Flutter
│
├── conformance/                     # suite de conformité partagée
│   └── cases/                       # JSON d'entrée + comportement attendu
│
└── .github/
    └── workflows/
        ├── java.yml
        ├── js.yml
        ├── dart.yml
        └── conformance.yml
```

Principe : **un dossier racine par écosystème** (`java/`, `js/`, `dart/`), chacun avec son propre outil de build natif. Le `spec/` est au-dessus de tous : c'est la source unique d'où le codegen produit les types de chaque langage.

## 2. Packaging par écosystème

| Package | Écosystème | Coordonnée de publication |
|---|---|---|
| servewright-core | Maven Central | `io.servewright:servewright-core` |
| servewright-spring-boot-starter | Maven Central | `io.servewright:servewright-spring-boot-starter` |
| servewright-react | npm | `@servewright/react` |
| servewright-react-shadcn | npm | `@servewright/react-shadcn` |
| servewright_flutter | pub.dev | `servewright_flutter` |
| servewright_flutter_material | pub.dev | `servewright_flutter_material` |

Note : le scope npm `@servewright` et le groupId Maven `io.servewright` supposent la propriété du domaine `servewright.io` (requise par Maven Central pour le namespace inversé). À sécuriser tôt.

## 3. Partition CI (critique pour les contributeurs)

Workflows séparés, **déclenchés par chemin** — un contributeur React n'installe jamais le JDK.

- `java.yml` : `on: push/pull_request` avec `paths: ['java/**', 'spec/**']` → setup JDK + `mvn verify`.
- `js.yml` : `paths: ['js/**', 'spec/**']` → setup Node + pnpm + `pnpm -r test`.
- `dart.yml` : `paths: ['dart/**', 'spec/**']` → setup Flutter + `dart test`.
- `conformance.yml` : `paths: ['spec/**', 'java/**', 'js/**', 'dart/**']` → exécute la suite de conformité contre les renderers. C'est lui qui garantit que « cross-client » n'est pas un mensonge.

`spec/**` déclenche tous les jobs : un changement de contrat doit revalider chaque implémentation.

## 4. Ordre de construction (le premier vertical slice AVANT tout le reste)

Ne PAS construire core complet → puis renderers → puis exemple. Construire **une tranche verticale minuscule de bout en bout** :

> Le serveur émet une primitive `Text`, sérialisée selon le contrat, rendue en React ET en Flutter.

Quand « le serveur dit `Text("hello")` et ça s'affiche dans les deux clients », toute la chaîne est prouvée. Tout le reste est de l'ajout incrémental sur une fondation qui marche.

Étapes du slice :
1. `spec/primitives/text.schema.json` + `spec/protocol/view.schema.json` — le contrat minimal.
2. `servewright-core` : `Primitive`, `Node`, `View`, sérialisation JSON d'un arbre contenant un seul `Text`.
3. `servewright-spring-boot-starter` : un endpoint `GET /servewright/view/{screen}` qui renvoie le `View`.
4. `servewright-react` : un renderer qui lit le `View`, mappe `Text` → un composant, l'affiche.
5. `servewright_flutter` : idem, `Text` → un `Widget`.
6. `examples/multi-step-form` (réduit au strict minimum) : les trois branchés ensemble.

Une fois ce slice vert : ajouter les primitives une par une, puis le binding/actions, puis les transitions/SSE. Jamais l'inverse.

## 5. Décisions d'outillage à figer au démarrage

- **JVM** : Maven multi-module (parent `pom.xml` dans `java/`). Java 21 LTS.
- **JS** : pnpm workspaces (meilleur que npm/yarn pour un monorepo multi-package). TypeScript strict.
- **Dart** : melos (gestionnaire de monorepo Dart) si plusieurs packages dart, sinon pub simple.
- **Codegen** : un script (Node ou Java) qui lit `spec/` et génère les types dans chaque package. Versionné, lancé en CI pour vérifier que le généré est à jour.
- **Versioning** : Changesets (JS) / version unifiée du monorepo. À décider, mais commencer en `0.x` partout.

## 6. Ce qui n'est PAS dans ce repo (rappel anti-scope)

- `servewright-mcp` (phase 3) → repo séparé futur.
- Le site vitrine (phase 2) → repo séparé futur.
- `servewright-react-mui`, `-tailwind`, `flutter-cupertino` (phase 2) → ajoutés ici plus tard, pas maintenant.