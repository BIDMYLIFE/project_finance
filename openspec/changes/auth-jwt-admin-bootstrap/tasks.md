## 1. Schema and Security Configuration

- [ ] 1.1 Confirm and document deployment defaults for cookie names, token TTLs, SameSite/CORS/CSRF topology, Argon2id parameters, and refresh-session retention before implementation
- [ ] 1.2 Create database migrations for organizations, users, ADMIN role association, auth sessions, and one-time bootstrap state
- [ ] 1.3 Add foreign keys, unique constraints, indexes, revoked/expiry timestamps, and bootstrap state locking needed for organization isolation and refresh rotation
- [ ] 1.4 Add typed security properties for `security.jwt.secret=${JWT_SECRET}`, token TTLs, cookie flags, and bootstrap availability without committing secret values

## 2. Identity Domain and Persistence

- [ ] 2.1 Implement organization, user, role, auth session, and bootstrap state entities with only `password_hash` and refresh token hashes persisted
- [ ] 2.2 Implement repositories for email lookup, organization-scoped queries, refresh-session lookup/revocation, and locked bootstrap-state access
- [ ] 2.3 Add DTOs and Bean Validation for bootstrap, login, refresh, logout, and authentication error responses

## 3. Password and JWT Security

- [ ] 3.1 Configure Argon2id password encoding and verify that plaintext passwords never enter persistence, response DTOs, logs, or audit payloads
- [ ] 3.2 Implement JWT signing and validation using the injected secret, required claims, configurable expiry, and startup failure when the secret is missing
- [ ] 3.3 Implement the Spring Security filter chain, authenticated principal, ADMIN authority, and organization context derived from validated access JWT claims
- [ ] 3.4 Configure protected-resource defaults and explicit public access for bootstrap, login, refresh, logout, and static authentication pages

## 4. Authentication Services

- [ ] 4.1 Implement transactional bootstrap service that creates one organization and its first ADMIN, locks the initialization state, and rolls back all data on failure
- [ ] 4.2 Implement login service with constant-shape failures for unknown email and incorrect password, access JWT creation, refresh token hashing, and session creation
- [ ] 4.3 Implement refresh service with expiry/revocation checks and atomic refresh-token rotation that rejects reuse
- [ ] 4.4 Implement logout service that revokes the current session, tolerates repeated logout, and never restores a revoked session
- [ ] 4.5 Apply organization scope in service operations so request-supplied organization identifiers cannot bypass the authenticated principal

## 5. API, MVC, and Cookie Boundary

- [ ] 5.1 Implement separate Web API controllers for bootstrap, login, refresh, and logout using the agreed HTTP status and response contracts
- [ ] 5.2 Implement separate MVC routes and Vue 3 views for the one-time bootstrap page and login page using local offline assets and explicit loading/error states
- [ ] 5.3 Centralize HttpOnly, Secure, SameSite, path, domain, set, and clear cookie behavior in an authentication cookie component
- [ ] 5.4 Implement centralized JSON exception handling for validation, authentication, authorization, expired/revoked session, conflict, and internal errors without stack traces or secrets

## 6. Verification and Security Tests

- [ ] 6.1 Add unit tests for Argon2id verification, JWT claims/expiry, constant-shape login failures, cookie construction, and safe error mapping
- [ ] 6.2 Add integration tests for successful/failed bootstrap, duplicate bootstrap, invalid credentials, login cookies, protected requests, and ADMIN authorization
- [ ] 6.3 Add refresh rotation tests covering valid refresh, expiry, revocation, reuse detection, and concurrent refresh attempts
- [ ] 6.4 Add logout tests covering session revocation, cookie clearing, refresh rejection after logout, and idempotent repeated logout
- [ ] 6.5 Add organization-isolation tests proving that an ADMIN cannot read or mutate another organization through path, query, body, or token-provided identifiers
- [ ] 6.6 Add concurrent bootstrap tests proving exactly one successful initialization and no partial organization/user data after failed attempts
- [ ] 6.7 Add security regression checks that scan API responses, logs, and persisted fixtures for plaintext passwords, token values, JWT secrets, database credentials, and stack traces

## 7. Documentation and Delivery Validation

- [ ] 7.1 Document required environment variables, secret rotation expectations, bootstrap activation behavior, cookie deployment prerequisites, and local offline startup
- [ ] 7.2 Verify API and MVC routes, frontend assets, and security configuration do not introduce CDN or external runtime resource dependencies
- [ ] 7.3 Run formatting, static checks, unit/integration/security tests, and the project build; record any deployment-specific open decisions before applying the change