# ADR 003 — Transitions patch by node id

**Status:** Accepted  
**Date:** 2026-06

## Context

Full-tree reloads break focus, scroll, and in-progress form input. JSON Pointer paths are fragile when tree shape changes.

## Decision

Post-initial-load updates use **Transition** objects: `{ basedOn, stateVersion, patches[] }`. Each patch targets a node **`id`**. V1 ops: `replace`, `insert`, `remove`, `setError`, `setLoading`.

`stateVersion` is monotonic. If `basedOn` ≠ client version, client **refetches** the full view.

Local unsubmitted field values win when a `replace` patch targets a dirty field.

## Consequences

- Stable `id` is mandatory for mutable nodes.
- `TransitionBuilder` rejects unknown target ids at build time (server).
- Transport (SSE default) is replaceable behind `Transport` / `TransitionPublisher` interfaces.
