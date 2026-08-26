## Context

See proposal.md for motivation. The repository already contains payment, payment category, allocation, receipt print, invoice and bank-account tables/entities, while the existing `payment-bank-posting` change defines the broader receipt and posting rules. The new UI must fit the existing Spring MVC/API layering, organization context, local WebJars and Vue page patterns.

## Goals / Non-Goals

**Goals:**

- Expose the existing payment domain through organization-safe DTOs, services and REST/MVC boundaries.
- Keep confirmation, allocation, posting, voiding and receipt printing transactional and auditable.
- Reuse existing invoice, bank-account and sequence contracts instead of duplicating domain state.
- Match the established offline classical UI and dashboard capability registry.

**Non-Goals:**

- External payment gateway, bank synchronization or automatic reconciliation.
- General ledger, expense management or reporting exports.
- Hard deletion of financial records.

## Decisions

- Use separate `PaymentApiController` and `PaymentsPageController`; controllers validate/assemble requests and delegate all organization and financial rules to services.
- Add request/response DTOs for payments, categories, allocations and receipt output. Do not expose JPA entities directly.
- Use a payment service transaction for confirmation plus allocation and invoice balance updates. Validate all referenced customer, category, invoice and bank-account records through organization-scoped repositories.
- Generate receipt numbers with the existing organization/year document sequence under a database lock. The same number is reused for receipt reprints.
- Keep pending deposit and posted state transitions explicit. Posting creates one auditable CREDIT transaction; voiding and account changes use reversal records as defined by `payment-bank-posting`.
- Build one Vue page with a focused API module and local Bootstrap/Vue/Axios/SweetAlert2 assets. Modal forms handle category management, payment entry, allocations and confirmation; the receipt view uses a print-friendly route/style.
- Update the capability registry only after the MVC route and page asset contract tests are present, so dashboard cannot expose a broken link.

Alternatives considered: exposing entities directly was rejected because it leaks organization and audit fields; putting payment rules in the controller was rejected because allocation and posting require transaction boundaries; using a new frontend framework was rejected because it violates the existing offline Vue architecture.

## Risks / Trade-offs

- [Risk] Payment allocation, invoice balances and bank posting span several aggregates → [Mitigation] keep the service transaction boundary explicit and add rollback/integration tests.
- [Risk] Receipt sequence contention or duplicate confirmation → [Mitigation] lock the sequence row, enforce uniqueness, and make confirmation state transitions idempotent or reject repeated confirmation.
- [Risk] Receipt print layout can overflow on long notes → [Mitigation] use fixed A4 print CSS, bounded text layout and a print-specific contract test.
- [Risk] Existing `payment-bank-posting` work is incomplete → [Mitigation] implement only the contracts required by this change and record integration points for the remaining posting work.
