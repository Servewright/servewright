# Servewright — Branding & Design System brief

> Lib open source de Server-Driven UI. Nom : **Servewright** (serve + playwright). Concept : le serveur écrit la pièce, le client la joue. Un contrat sémantique unique → plusieurs clients (React, Flutter) → design system interchangeable.

Ce document sert de brief direct pour générer le logo et le design system (utilisable tel quel dans Claude Design).

---

## 1. Personnalité de marque

- **Registre** : outil d'infrastructure pour développeurs. Sérieux, précis, sans esbroufe. Pensez Vite, Biome, Turborepo, Playwright — pas une app grand public colorée.
- **Ton** : technique mais accessible, un brin malin (le nom est un jeu de mots assumé).
- **Ce qu'il faut éviter** : dégradés tape-à-l'œil, multicolore, illustrations 3D, glassmorphism, tout ce qui date vite. La sobriété est un signal de sériosité d'infra.

## 2. Concept d'icône

Métaphore : *playwright* (dramaturge) → le serveur façonne ce que le client joue.

Direction principale — **le chevron-scène** : un chevron `>` (le serveur / le « serve ») dont l'ouverture cadre une scène ou un rideau stylisé. Lie « serveur » et « pièce jouée ». Doit rester lisible à 16x16 px.

Favicon / avatar — **monogramme `Sw`** : S et w liés, le w évoquant une vague de propagation (le push serveur→clients). L'ADN visuel commun aux deux est le chevron `>`.

Exigences :
- SVG vectoriel, net de 16px à grande taille.
- Doit fonctionner en **monochrome** (une seule couleur) pour terminal/README dark+light.
- Trois variantes livrables : icône seule, icône + wordmark horizontal, version monochrome.

## 3. Palette

Sobre : 1 neutre + 1 accent maximum.

- **Neutres** : une échelle de gris froids (du quasi-noir `#0A0B0D` au blanc cassé), pour texte, fonds, bordures. Adaptatif dark/light.
- **Accent (1 seul)** : suggestion d'un **vert-émeraude profond** ou **indigo** — évite le bleu générique des libs front et le violet sur-utilisé en 2025-2026. L'accent sert UNIQUEMENT aux états actifs, liens, et au logo couleur. Jamais en aplat de fond large.
- Pas de seconde couleur d'accent en V1.

## 4. Tokens (design system)

Système de tokens sémantiques (pas de valeurs en dur), exportables en CSS variables + JSON.

**Échelle d'espacement** (base 4px) : `space-1`=4, `space-2`=8, `space-3`=12, `space-4`=16, `space-6`=24, `space-8`=32, `space-12`=48, `space-16`=64.

**Typographie** :
- Police de caractères : une sans-serif géométrique-neutre pour l'UI (type Inter / Geist) + une mono pour le code (type JetBrains Mono / Geist Mono).
- Échelle : `text-xs` 12 / `text-sm` 14 / `text-base` 16 / `text-lg` 18 / `text-xl` 24 / `text-2xl` 32 / `text-3xl` 48. Line-height confortable (1.5 corps, 1.2 titres).
- Poids : 400 corps, 500 medium, 600 semibold titres. Pas de 700+ (trop lourd pour de l'infra).

**Rayons** : `radius-sm` 4 / `radius-md` 8 / `radius-lg` 12 / `radius-full` 9999. Défaut UI = `radius-md`.

**Élévation** : ombres discrètes, 2 niveaux max (`shadow-sm`, `shadow-md`). Privilégier les bordures 1px aux ombres lourdes.

**Couleurs sémantiques** (mappées sur neutres + accent) : `bg`, `bg-subtle`, `fg`, `fg-muted`, `border`, `accent`, `accent-fg`, `success`, `warning`, `danger`, `info`. Chacune en variante light + dark.

## 5. Composants à produire (priorité pour la démo dashboard)

Aligner sur les ~10 primitives de la lib (le design system doit pouvoir les rendre) :
- Button (variants : primary, secondary, danger ; états : default, hover, disabled, loading)
- TextInput (label, placeholder, états : default, focus, error, validating)
- Select, Checkbox
- Card / Stat (métrique avec label, valeur, delta up/down)
- Table (lecture seule, en-têtes, lignes, empty state)
- Text (heading / body / caption / muted)
- Container / Group (layout, titre de section)

Chaque composant en light + dark.

## 6. Livrables attendus de Claude Design

1. Logo : chevron-scène (principal) + monogramme `Sw` (favicon), en SVG, mono + couleur.
2. Page de tokens (couleurs, espacement, typo, rayons) en CSS variables + JSON.
3. Planche de composants ci-dessus, états inclus, light + dark.
4. Un écran de démo « dashboard » assemblant Stat + Table + filtres (Select/Button) pour valider la cohérence d'ensemble.

## 7. Prompt prêt à coller dans Claude Design

> Crée un design system et un logo pour « Servewright », une librairie open source de Server-Driven UI pour développeurs (le serveur décrit l'UI, des clients React et Flutter la rendent). Personnalité : outil d'infra dev, sobre et précis, à la Vite / Playwright / Biome — pas grand public. Logo : un chevron « > » dont l'ouverture cadre une scène/rideau stylisé (métaphore du dramaturge : le serveur écrit la pièce, le client la joue), plus un monogramme « Sw » pour le favicon ; livre-les en SVG monochrome et couleur. Palette : gris froids neutres + un seul accent (émeraude profond ou indigo, évite le bleu générique). Typo : sans-serif neutre (Inter/Geist) + mono pour le code. Tokens : espacement base-4, rayons 4/8/12, ombres discrètes, couleurs sémantiques en light + dark. Produis : le logo, une planche de tokens, et les composants Button, TextInput, Select, Checkbox, Stat/Card, Table, Text, Container — chacun avec ses états et en light + dark. Termine par un écran de dashboard de démonstration assemblant des cartes de stats, une table et des filtres.