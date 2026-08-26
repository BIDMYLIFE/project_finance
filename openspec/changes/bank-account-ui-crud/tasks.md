## 1. Backend contract and service

- [x] 1.1 Add `BankAccountRequest` and `BankAccountResponse` DTOs with Bean Validation for required name, uppercase three-letter currency code, and four-decimal monetary balance limits.
- [x] 1.2 Implement a dedicated `BankAccountService` using the authenticated organization context for paginated listing, create, update, duplicate-name handling, and active/inactive filtering.
- [x] 1.3 Add REST endpoints under `/api/v1/bank-accounts` for GET list, POST create, PUT update, and DELETE deactivate with the project's standard response and error behavior.
- [x] 1.4 Add service and API tests covering validation, duplicate names, organization isolation, update, deactivation idempotency, pagination, and safe response fields.

## 2. Banking page and localized frontend

- [x] 2.1 Add the MVC `/banking` controller and all required English/Traditional Chinese message keys for headings, filters, table, form, statuses, errors, confirmations, and pagination.
- [x] 2.2 Add local `banking-api.js`, `pages/banking.js`, and `banking.css` modules following the Products/Customers loading, error, modal, pagination, validation, and SweetAlert2 patterns.
- [x] 2.3 Add a responsive `/banking` template with account list, search/status filters, create/edit modal, deactivate action, accessible form labels, and mobile-safe layout.
- [x] 2.4 Add frontend resource, i18n, offline-asset, and banking workflow tests for loading/empty/error states, CRUD wiring, local resources, and responsive markup.

## 3. Dashboard integration and verification

- [x] 3.1 Change the Dashboard banking capability to available with route `/banking` while preserving other capability routes and ownership metadata.
- [x] 3.2 Add Dashboard and navigation tests proving the Banking link resolves to `/banking` and the banking page is protected and reachable through the MVC route.
- [x] 3.3 Run the complete Maven test suite, package build, OpenSpec validation, and available browser/mobile checks; document any remaining browser-specific layout risk.
