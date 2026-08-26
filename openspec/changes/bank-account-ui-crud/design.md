## Context

The database migration, `BankAccount` entity, and repository already exist, including organization ownership, active state, account name uniqueness, currency code, and opening balance. There is no application service, DTO/API surface, MVC page, or frontend module for bank accounts. Existing Products and Customers pages establish the project's CRUD, pagination, validation, localization, offline asset, and deactivation patterns.

## Goals / Non-Goals

**Goals:**

- Add a layered bank-account management flow from MVC/API controllers through service to repository.
- Keep every read and write organization-scoped through the authenticated organization context.
- Reuse the existing paginated response, error response, local WebJar assets, Vue page structure, and SweetAlert2 confirmation behavior.
- Make `/banking` the available Dashboard capability route.

**Non-Goals:**

- Do not add bank transaction entry, reconciliation, external bank synchronization, or account balance calculation.
- Do not hard-delete accounts or alter payment/bank transaction posting rules.
- Do not add a new frontend dependency or database migration.

## Decisions

### Use a dedicated bank-account service and DTO contract

Add bank-account request/response DTOs, a service for validation and organization-scoped business rules, a REST controller under `/api/v1/bank-accounts`, and an MVC controller for `/banking`. This preserves `Controller -> Service -> Repository` and avoids exposing the entity. Reusing `MasterDataService` is rejected because the banking account lifecycle is a distinct financial domain and would couple unrelated responsibilities.

### Use soft deactivation for CRUD delete

Map DELETE to the existing entity `deactivate()` operation and filter list results by the requested active status. This preserves historical references and matches the existing financial design. Hard deletion is rejected because bank transactions and future payment references may depend on the record.

### Reuse the Products page interaction model

The banking page will use a paginated table, active/inactive selector, keyword search, Bootstrap modal for create/edit, client-side basic validation backed by Bean Validation, centralized API calls, and SweetAlert2 for confirmation/results. A separate `banking-api.js`, `pages/banking.js`, and `banking.css` keeps API URLs and page state out of the template while following the established offline structure.

### Enable the existing Dashboard capability

Change only the banking registry entry from unavailable/null route to available `/banking`, and add the banking message keys needed by the page/controller. Existing Dashboard capability ownership and other unavailable capabilities remain unchanged.

## Risks / Trade-offs

- [Risk] Existing payment workflows may select inactive accounts if they do not filter explicitly → Mitigation: keep service-level active-account selection rules unchanged or add focused active-account contract checks where the workflow consumes accounts.
- [Risk] Account names may contain visually confusing whitespace or casing variants → Mitigation: trim input and enforce the existing case-insensitive organization uniqueness rule; return a standard business error on conflicts.
- [Risk] No browser automation dependency is currently configured → Mitigation: add resource/contract tests and, if no browser runner is available during implementation, document the remaining viewport verification risk.

## Migration Plan

No database migration is required. Deploy backend and static/template changes together, then verify Dashboard navigation, list/create/update/deactivate flows, organization isolation, and offline asset tests. Rollback consists of reverting the application changes; existing bank account rows remain compatible.
