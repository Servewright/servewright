# Servewright — Architecture & Design Decisions

> Lib Server-Driven UI : un contrat sémantique unique, piloté par le backend, rendu nativement sur plusieurs clients (React, Flutter) avec le design system de leur choix.

Ce document acte les décisions de conception. Il sert de boussole et de protection anti-scope. Toute idée hors périmètre va dans la roadmap, jamais dans la V1.

---

## 1. Thèse du projet

**Un seul contrat sémantique → plusieurs clients → design system interchangeable, le tout piloté serveur.**

La V1 doit rendre cette phrase indéniable. Tout ce qui ne sert pas directement à la prouver sort de la V1.

Positionnement : lib d'infrastructure open source, idiomatique, propre, facile à implémenter et à contribuer. L'innovation est concentrée sur le **contrat SDUI** ; tout le reste utilise les conventions établies (« ne pas réinventer la roue »).

---

## 2. Règle d'or (non négociable)

**Le protocole est 100 % sémantique, jamais présentationnel.**

Le serveur émet `TextInput { id, label, value, required }`. Il n'émet **jamais** de `className`, de couleur, de classe Tailwind, ni aucune information de rendu.

Conséquences directes :
- L'adaptabilité aux design systems (shadcn / MUI / Tailwind / Material) tombe **gratuitement** : c'est uniquement « quel composant la registry cliente associe au type de primitive ». Le serveur ne change pas d'une ligne.
- Les primitives custom locales deviennent triviales (une entrée de registry, des deux côtés).
- Si une miette de présentation entre dans le protocole, l'adaptabilité meurt. C'est le critère qu'un lecteur senior vérifie en premier.

---

## 3. Décisions actées

### 3.1 Modèle d'état
- **Pas d'event sourcing, pas d'Axon.** Accidental complexity pour ce besoin. Le serveur tient l'état courant via une **machine à états explicite**.
- CQRS allégé : séparation command (mute l'état) / query (calcule l'arbre UI), sans event store ni replay.
- L'event sourcing ne serait justifié que si l'audit/replay devenait une feature vendue — ce n'est pas le cas.

### 3.2 Contrat & schéma
- **Format du contrat : JSON Schema** (lisible = argument de vente, cohérent avec le payload JSON sur le fil, codegen mûr vers Dart/TS/Java).
- Protobuf écarté : gain binaire non pertinent pour un transport SSE/JSON, friction de chaîne, perte de lisibilité.
- Le JSON Schema est un **artefact généré**, jamais écrit à la main. Le dev déclare ses primitives via un **builder de code typé** (Java + TS), autocomplété et validé à la compilation.

### 3.3 Extensibilité — registries symétriques
Trois registries, cœur du système :
- **Registry serveur** (`servewright-core`) : déclare les primitives, leur schéma, leur validation. Sert à émettre un arbre UI valide.
- **Registry de rendu client** (`servewright-react`, `servewright-flutter`) : associe `type de primitive → composant concret`. Override granulaire (last-write-wins).
- Le **contrat JSON Schema** est la frontière : extensible côté dev, le serveur valide contre, le client rend selon.

Une **primitive custom locale** = une entrée serveur (schéma + validation) + une entrée client (composant). Aucune publication upstream requise. Techniquement identique à une primitive standard ; les standards sont juste « celles que Servewright pré-enregistre ».

**Garantie :** une primitive custom est aussi typée et validée qu'une standard. La liberté porte sur le *vocabulaire*, jamais sur la capacité à casser le contrat. Une primitive malformée doit échouer proprement au build, jamais silencieusement chez l'utilisateur final.

### 3.4 Principe core/sucre (deux niveaux, structurant)

**Tout point d'extension existe en deux niveaux : une API programmatique explicite dans `servewright-core` (la vérité, framework-agnostique), et un sucre déclaratif/annoté par-dessus dans `servewright-spring-boot-starter` (l'ergonomie, optionnel). Le sucre construit toujours l'API programmatique ; il ne la remplace jamais.**

Trois invariants non négociables :
1. Le core ne scanne rien, n'a aucune annotation, aucune DI. Un mécanisme exigeant du scan de classpath → vit dans le starter.
2. Le sucre est un raccourci, jamais une porte unique. Tout ce que le sucre permet doit être faisable sans lui (routing dynamique, multi-tenant, cas tordus passent par l'API programmatique).
3. Échec au boot, jamais au runtime client (collisions, références à des ids inexistants, handlers en double → erreur explicite au démarrage).

**Critère pour ajouter du sucre** (le principe est universel, son application est priorisée) :
- Oui si l'API programmatique est verbeuse/répétitive ou qu'une annotation est attendue par le dev Spring (routing, validation async).
- Non si l'API est déjà fluide et appelée une fois (builder de primitive, de transition, config transport). Une annotation n'ajouterait que de la complexité.

Garde-fou : ne jamais sucrer un confort dont personne n'a senti le manque. La retenue s'applique aussi au sucre.

**Routing d'actions — application de référence :**
- Core : `ActionRouter.builder().on(type, target, handler)` — explicite, testable, agnostique. Remplace tout `switch` sur `action.type()`.
- Starter : `@ServewrightController` + `@OnAction(type, target)` + `@Payload` (binding de payload typé dans un objet métier, vrai gain ergonomique au-delà du routing) + `@OnAsyncValidation` pour les validateurs async.
- Garde-fous : annotations non obligatoires, collision = erreur au boot, log des routes découvertes au démarrage.

**Portée V1 du sucre :** routing d'actions (`@OnAction` + `@Payload`) + validation async (`@OnAsyncValidation`) **uniquement**. `@ServewrightPrimitive` (primitive custom annotée) = phase 2. Tout le reste (transitions, vues, sérialisation) = API programmatique seule en V1.

### 3.5 Extensibilité — mécanismes (pas d'héritage, pas de magie)
- Extensibilité par **composition** : interfaces fonctionnelles, registry, builder, SPI `ServiceLoader`. **Pas d'héritage** des classes de la lib (anti-pattern : fragile base class).
- `servewright-core` : **Java pur, zéro dépendance framework**, idiomatique JDK. Règle de discipline : à chaque ajout, vérifier « est-ce que ça compile et tourne *sans* Spring sur le classpath ? ». Si non → ça va dans le starter.
- `servewright-spring-boot-starter` : conventions Spring complètes (auto-config, `@ConfigurationProperties`, customizers façon `WebMvcConfigurer`, annotations là où c'est le pattern attendu). L'ergonomie « zéro config par défaut, override chirurgical ».

### 3.6 Packaging des design systems
- **Un package par design system**, jamais un monolithe activable (poids du bundle, arbres de dépendances, cycles de vie divergents).
- `servewright-react` / `servewright-flutter` = moteur (registry + renderer + types), **sans** design system.
- `servewright-react-shadcn`, `servewright-react-mui`, `servewright-flutter-material`, … = adaptateurs séparés, chacun avec **sa** peer-dependency. Opt-in.
- Override : le dev importe un adaptateur puis écrase entrée par entrée (`register(type, component)`).
- **Plusieurs adaptateurs** : ordre d'enregistrement = priorité (last-write-wins). Mixer deux design systems entiers n'est **pas** un cas supporté (mauvaise pratique UX) — le cas réel est « base + overrides ». À documenter, pas à coder.

### 3.7 Transport, perf & frontières de responsabilité
**Servewright optimise le contrat et le transport ; l'implémenteur optimise son infra.**

Responsabilité de Servewright :
- **Transitions / diff plutôt que full-tree** : après l'état initial, pousser ce qui change (JSON Patch RFC 6902 ou diff typé maison), pas l'arbre entier.
- **Identité stable des nœuds** (id par primitive) : prérequis du diff et de la préservation de l'état local (focus, scroll).
- **Transport abstrait** (interface remplaçable), **SSE par défaut** en V1 ; WS / polling / gRPC-stream branchables.
- **Sérialisation abstraite**, **JSON par défaut** ; binaire (CBOR/MessagePack) branchable plus tard.
- Caching du **contrat** (schéma + état initial) via ETag / Cache-Control générés par le starter.

**Hors Servewright** (l'implémenteur branche ses outils) : cache de données (Redis/Caffeine), base de données, scaling horizontal, load balancing, infra. Servewright fournit les **hooks**, pas les implémentations.

**Interdiction :** pas de benchmark ni d'optimisation en V1 (aucune donnée réelle = optimisation prématurée). On *pose les frontières* qui rendent la perf possible ; on mesure et on optimise seulement avec un utilisateur réel (phase 2+, via le dashboard démo).

---

## 4. Points structurants à intégrer au CONTRAT V1

Ce sont des **décisions de contrat**, pas des features. Les acter coûte une phrase ; les découvrir plus tard casse le protocole et toutes les apps déployées.

1. **Actions & data-binding** (le plus important). Définir comment l'état d'une saisie remonte (à la frappe vs au submit, où vit l'état intermédiaire) et comment un clic déclenche une action serveur identifiée. À trancher avant les primitives.
2. **Validation déclarative** dans la primitive (`required`, `min`, regex…), exécutable client (UX) **et** re-vérifiée serveur (confiance).
3. **Fallback sur primitive inconnue** : une app déployée recevant une primitive d'un schéma plus récent doit l'**ignorer gracieusement avec placeholder**, jamais crasher. C'est le corollaire obligatoire de « changer l'UI sans redéployer l'app ».
4. **Resync** : numéro de version d'état pour détecter le décalage au reconnect (SSE coupée, réseau mobile) et refetcher.
5. **Hooks d'accessibilité** dans les primitives (labels, rôles, ordre de focus) : sinon toute app Servewright est inaccessible par construction — rédhibitoire pour des clients sérieux.

---

## 5. À documenter en V1 (implémentation différée possible)

- **Threat model sécurité** : un protocole où le serveur pilote l'UI cliente est une surface d'injection. Autorisation (quelles primitives un user peut recevoir), sanitization du contenu. Crédibilité fintech/régulé oblige.
- **Suite de conformité** : un set de cas « ce JSON → ce rendu / ce comportement » que tout renderer doit passer. C'est la version exécutable de la spec ; sans elle, « cross-client » dérive et devient un mensonge. Permet à des tiers d'écrire des renderers (Vue, SwiftUI…) sans casser la compat.
- **Golden path contributeur** : ajouter une primitive de bout en bout (schéma → validation serveur → renderer React → renderer Flutter → test de conformité) documenté en une page + scaffold.
- **Partition CI par package** : un contributeur front ne doit pas installer le JDK pour tester React. Build/CI séparés par langage.

---

## 6. Différé (ne pas se condamner, mais pas urgent)

- États **loading / error** standardisés dans le contrat.
- **i18n** : libellés traduits serveur vs clés résolues client (décision de contrat à acter).
- **GOVERNANCE.md** : barre primitive standard vs locale, ce qui est ouvert dans le core.

---

## 7. Méta-règle anti-scope

> **La phase N+1 ne commence pas tant que la phase N n'est pas finie et montrable.**

Le mode d'échec n°1 (contexte solo, charge perso) : ouvrir trop de fronts en parallèle, rien n'atteint « fini ». Une V1 finie et déployée écrase une vision à 30 % étalée sur trois phases.

La compétence que ce projet démontre n'est pas « j'ai tout mis », c'est « j'ai conçu une frontière si propre que tout devient *possible* sans que je l'aie tout codé ». L'architecture doit **permettre** le multi-adaptateur, le MCP, les composites ; la V1 ne les **livre** pas.