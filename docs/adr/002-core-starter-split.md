# ADR 002 — Core vs Spring Boot starter split

**Status:** Accepted  
**Date:** 2026-06

## Context

Servewright must be usable outside Spring (tests, other JVM frameworks, libraries).

## Decision

- **`servewright-core`**: Java 21, zero Spring dependency. View/transition model, validation, `ActionRouter`, serializers, `TransitionBuilder`.
- **`servewright-spring-boot-starter`**: Auto-configuration, SSE endpoints, `@OnAction` scanning, Jackson adapters.

Declarative sugar always delegates to programmatic APIs in core.

## Consequences

- Any feature requiring classpath scanning or DI belongs in the starter.
- Core unit tests run fast without Spring context.
- Contributors verify `mvn -f java/servewright-core verify` when touching domain logic.
