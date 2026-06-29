# PHASE 5 — Suite de conformité, doc contributeur & durcissement V1

## Objectif
Transformer le prototype fonctionnel en lib publiable et contribuable. Pas de nouvelle feature — consolidation, garanties, documentation. Clôture de la V1.

## Livrables
- **Suite de conformité complète** : pour chaque primitive et chaque op de transition, un cas `entrée JSON → comportement/rendu attendu`, exécuté contre React ET Flutter en CI. C'est la version exécutable de la spec : un nouveau renderer (Vue, SwiftUI…) doit pouvoir la passer.
- **Fallbacks robustes** : primitive inconnue, prop manquante, schemaVersion plus récente côté serveur → comportement gracieux testé (jamais de crash client).
- **Hooks d'accessibilité** : labels/rôles/focus transitant dans les primitives, rendus correctement (test a11y minimal par renderer).
- **Doc contributeur** : `CONTRIBUTING.md`, ADRs (décisions clés), et le "golden path" documenté : comment ajouter une primitive de bout en bout (schéma → core → 2 renderers → cas de conformance), avec une primitive de référence comme template.
- **Threat model** documenté : surface d'injection (serveur pilote l'UI), autorisation, sanitization — au moins le document, implémentation des garde-fous évidents.
- **Packaging publiable** : coordonnées Maven/npm/pub correctes, versions `0.x`, README de chaque package.

## Tests exigés
- **Conformance exhaustive** : tous les cas verts sur les 2 renderers en CI.
- **Robustesse** : tous les cas de fallback (inconnu, malformé, version) testés et gracieux.
- **A11y** : test minimal par renderer (un champ a son label/rôle).
- **Build de publication** : `mvn package`, `pnpm build`, `dart pub publish --dry-run` réussissent.

## Critères de sortie (gate)
- [ ] Tests Phases 0–4 toujours verts.
- [ ] Suite de conformance couvre 100% des primitives et ops, verte sur React ET Flutter en CI.
- [ ] Tous les fallbacks testés et gracieux (aucun crash possible côté client).
- [ ] CONTRIBUTING.md + ADRs + golden path + primitive-template présents.
- [ ] `--dry-run` de publication réussit sur les 3 écosystèmes.
- [ ] Threat model documenté.
- [ ] La démo `examples/multi-step-form` tourne complètement (formulaire multi-étapes, validation, transitions SSE) en React et Flutter.

## Fin de V1
À la sortie de cette gate, la V1 est complète et publiable. Les phases suivantes (dashboard démo, site, adaptateurs MUI/Tailwind/Cupertino, MCP) relèvent de la roadmap V2/V3 — NE PAS les entamer dans la même session sans décision explicite.
