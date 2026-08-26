## 1. Authentication Preconditions and Contracts

- [x] 1.1 Verify `auth-jwt-admin-bootstrap` login success, protected MVC route, refresh, logout idempotency, cookie clearing, `401/403` error contract, and authenticated organization context
- [x] 1.2 Confirm the existing customer-management route contract from `financial-erp-core`; record product, quote, invoice, payment, banking, and reporting entries as unavailable until their owning changes expose routes
- [x] 1.3 Confirm whether an existing server-safe identity view model/API can be reused; if not, define and implement the minimal protected `GET /api/v1/auth/me` DTO without credentials or client-supplied scope
- [x] 1.4 Add contract tests proving identity and organization values come only from the authenticated server context and forged request/local-state identifiers are ignored

## 2. Shared Frontend API and Session State

- [x] 2.1 Centralize API base URL, timeout, `withCredentials`, request state, and error DTO mapping in a shared Axios service
- [x] 2.2 Implement a single controlled refresh attempt for eligible `401` responses, excluding login/refresh/logout requests and preventing redirect loops
- [x] 2.3 Implement authentication-failure handling for unauthorized, expired, and failed-refresh responses, including clearing frontend transient state and redirecting to `/auth/login` with only a non-sensitive return path
- [x] 2.4 Implement shared loading, success, validation-error, network-error, empty, retry, disabled, and logout state conventions for Vue pages
- [x] 2.5 Add unit/API tests for timeout, credentials, `401` refresh success/failure, `403` handling, retry limits, safe error messages, and redirect guards

## 3. Protected Dashboard and Post-Login Routing

- [x] 3.1 Add the protected `/` MVC page route and Dashboard view boundary without exposing page content to unauthenticated requests
- [x] 3.2 Update the login success flow to navigate to `/` after authentication cookies are set and preserve only a validated non-sensitive return path when applicable
- [x] 3.3 Load the server-authenticated identity source on Dashboard startup and render a safe user/organization summary with loading, error, empty, and unauthorized states
- [x] 3.4 Implement logout command wiring to the existing logout API/service, guarantee cookie-clearing behavior, and navigate to `/auth/login` for valid, expired, missing, or repeated sessions
- [x] 3.5 Add Dashboard MVP content that provides identity, capability status, and entry points only; ensure no undefined or fabricated live financial summary request/data is present

## 4. Navigation and Capability Boundaries

- [x] 4.1 Define the centralized capability registry for Dashboard, customer management, product, quote, invoice, payment, banking, and reporting labels, owners, routes, and availability
- [x] 4.2 Add the available customer-management entry using the route contract owned by `financial-erp-core`, preserving server authentication and organization scope
- [x] 4.3 Render unavailable detail capabilities as accessible disabled/coming-soon items that do not navigate to guessed routes or call undefined APIs
- [x] 4.4 Verify Dashboard does not duplicate customer, product, quote, invoice, payment, banking, reporting detail pages, domain rules, or financial calculations
- [x] 4.5 Add navigation tests for available, unavailable, unauthorized, keyboard, touch, and route-preservation behavior

## 5. Offline Responsive Accessible UI

- [x] 5.1 Verify and wire local Vue 3, Bootstrap 5.3, Axios, SweetAlert2, font, and icon assets with locked versions and no CDN/external runtime URLs
- [x] 5.2 Implement responsive Bootstrap layout and mobile navigation with stable dimensions, visible focus, disabled/loading states, accessible names, tab order, and status announcements
- [x] 5.3 Use SweetAlert2 for transient operation feedback while retaining accessible in-page error/session status for persistent failures
- [x] 5.4 Add browser checks that block external network and confirm Dashboard still renders from local assets
- [x] 5.5 Add desktop and mobile browser checks for no-overlap, readable text, keyboard navigation, touch navigation, error/retry states, logout, and session redirect flows

## 6. Verification and Delivery

- [x] 6.1 Run authentication integration tests for login-to-dashboard, protected `/`, identity DTO safety, session expiry, refresh failure, and logout cookie clearing
- [x] 6.2 Run browser tests for login success redirect, unauthorized redirect, controlled refresh, no-summary MVP, customer entry, unavailable links, logout, and no redirect loop
- [x] 6.3 Verify responses, logs, frontend state, local storage, session storage, and downloaded/runtime assets contain no password, token, secret, database credential, or unsafe organization data
- [x] 6.4 Verify the Dashboard does not issue requests to an undefined dashboard summary API and that future summary integration remains behind an explicit contract
- [x] 6.5 Run formatting, static checks, unit/API/browser tests, offline asset checks, and the full build; record only failures introduced by this change
- [x] 6.6 Document deployment order with `auth-jwt-admin-bootstrap` first, customer-route dependency, unavailable capability behavior, offline startup, and rollback without deleting authentication or financial data
