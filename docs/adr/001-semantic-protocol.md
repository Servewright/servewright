# ADR 001 — Semantic-only protocol

**Status:** Accepted  
**Date:** 2026-06

## Context

Servewright targets multiple clients and design systems. A presentation-aware protocol would couple server payloads to CSS frameworks and block adapter swapping.

## Decision

The wire contract is **100% semantic**. Primitives carry identity (`id`), type, and behavioral props (labels, values, validation rules, action targets). No colors, class names, layout pixels, or design tokens.

## Consequences

- Design system packages (`@servewright/react-shadcn`, `servewright_flutter_material`) own all visual rendering.
- Custom primitives are equally valid: one schema entry + one registry entry per client.
- Conformance tests assert behavior and accessible names, not screenshots.
