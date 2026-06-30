# Threat model — Servewright V1

Servewright lets the **server drive client UI**. That is powerful and introduces trust boundaries. This document scopes risks for V1 integrators.

## Assets

- End-user data entered in forms
- Session/authentication state of the host application
- Integrity of the client UI (what the user sees and clicks)

## Trust boundaries

```
[Browser / Mobile app]  ←→  [Servewright endpoints]  ←→  [Application domain]
     untrusted wire              must authenticate              trusted logic
```

The client must treat view JSON and transitions as **untrusted input** unless the transport is authenticated and authorized like any other API.

## Threats & mitigations

### T1 — Unauthorized view/action access

**Risk:** Attacker fetches or mutates screens they should not see.

**Mitigations (integrator responsibility):**
- Protect `/servewright/view/*`, `/servewright/action`, `/servewright/stream/*` with the same authn/authz as business APIs.
- Bind `screen` identifiers to server-side authorization (user A cannot load user B's screen).
- Starter does not implement auth; applications must add Spring Security or equivalent.

### T2 — UI injection via primitive props

**Risk:** Server (or compromised server) emits malicious `content` rendered as HTML/script.

**Mitigations:**
- React adapter uses React's default escaping for text nodes; do not use `dangerouslySetInnerHTML` in stock primitives.
- Flutter `Text` widgets escape by default.
- **Do not** add raw HTML primitives without a sanitization policy.
- Validate all inbound actions server-side; never trust client-only validation.

### T3 — Action payload tampering

**Risk:** Client posts crafted `Action` payloads to bypass validation.

**Mitigations:**
- `ActionCommandHandler` re-validates every payload against the current view tree.
- Stale `stateVersion` triggers resync, not silent apply.
- Rate-limit action endpoints in production.

### T4 — SSE stream hijacking / cross-tenant leaks

**Risk:** SSE connection receives another user's transitions.

**Mitigations:**
- Authenticate SSE connections; scope `SseTransitionPublisher` subscribers per session/tenant.
- Use HTTPS; set appropriate CORS in browser apps.

### T5 — Schema downgrade / unknown primitives

**Risk:** Older client crashes on newer server vocabulary.

**Mitigations (built into V1):**
- Unknown primitive types render a **placeholder**, never throw.
- Newer `schemaVersion` still renders (forward-compatible clients).
- Conformance cases cover unknown primitive and newer schema.

### T6 — Denial of service

**Risk:** Oversized view trees or transition floods.

**Mitigations (integrator):**
- Request size limits on POST `/servewright/action`.
- SSE connection limits; transition payload size caps at reverse proxy.

## Out of scope V1

- Built-in CSP headers for embedded web views
- Field-level encryption
- Signed transitions (HMAC) — consider for regulated environments in V2

## Reporting

Report security issues privately to the maintainers before public disclosure.
