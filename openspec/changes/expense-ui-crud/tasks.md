## 1. Expense Data Model

- [x] 1.1 Extend the existing `SOUTHWND.expenses` schema with category, actor, payee, note, lifecycle timestamps, status constraints, positive amount constraint, foreign keys, and organization/date/status/category indexes.
- [x] 1.2 Add `Expense` entity mappings and lifecycle methods for `DRAFT`, `CONFIRMED`, and `VOIDED` without exposing delete operations.
- [x] 1.3 Add organization-scoped Expense repository queries for list filters, detail lookup, state lookup, and source/reversal duplicate guards.

## 2. Expense Services and APIs

- [x] 2.1 Add validated Expense request/response DTOs and bounded list query parameters for category, bank account, status, date, keyword, sorting, and pagination.
- [x] 2.2 Implement Expense Service create/list/update operations with active category/account, currency, amount, date, and organization validation.
- [x] 2.3 Implement atomic confirmation that validates an active compatible bank account and category, creates a source-linked `DEBIT`, and changes the expense to `CONFIRMED`.
- [x] 2.4 Implement draft/confirmed void operations using status transitions and bank reversal chains without hard deletion.
- [x] 2.5 Add protected `/api/v1/expenses` endpoints for list, create, update, confirm, and void with centralized validation and error responses.
- [x] 2.6 Add `/expenses` MVC page route and ensure unauthenticated/non-ADMIN access follows the existing security contract.

## 3. Dashboard and Offline Web UI

- [x] 3.1 Add the `expenses` capability, `/expenses` route, owner metadata, and localized Dashboard navigation messages.
- [x] 3.2 Add local Expense API/page modules and template/styles using Vue 3, Bootstrap 5.3, Axios, and SweetAlert2 without CDN references.
- [x] 3.3 Implement responsive Expense list/form UI with active category and bank-account selection, create/edit/confirm/void actions, and loading/empty/error/success/disabled/retry states.
- [x] 3.4 Add accessibility and mobile layout checks for the Dashboard Expense link and Expense page controls.

## 4. Verification

- [x] 4.1 Add entity/repository integration tests for constraints, organization isolation, filters, pagination, lifecycle states, and source references.
- [x] 4.2 Add service tests for validation, category/account compatibility, atomic confirmation rollback, idempotency guards, and reversal behavior.
- [x] 4.3 Add API/security contract tests for CRUD, state transitions, centralized errors, unauthenticated access, non-ADMIN access, and cross-organization IDs.
- [x] 4.4 Add Dashboard and browser asset tests for the Expense entry, route, localized messages, offline resources, responsive controls, and no-overlap behavior.
- [x] 4.5 Run formatter, relevant tests, OpenSpec strict validation, full Maven test/package, and secret/CDN scans.
