## Why

The ERP already has an organization-scoped `BankAccount` entity and database table, but users cannot maintain bank accounts through the application. Adding the management page and CRUD API now exposes the banking capability consistently and gives payment/banking workflows a usable account master-data entry point.

## What Changes

- Add an organization-scoped bank account management capability with list, pagination, active/inactive filtering, create, edit, and deactivate operations.
- Add REST DTOs, validation, service rules, API endpoints, and MVC page routing for bank accounts.
- Add an offline Vue/Bootstrap/SweetAlert2 page following the existing Customers/Products management patterns.
- Enable the Dashboard banking capability and update its navigation route to `/banking`.
- Preserve inactive records for historical references; do not introduce hard deletion or change existing payment/bank transaction behavior.

## Capabilities

### New Capabilities

- `bank-account-ui-crud`: Organization-scoped bank account maintenance, including API, responsive UI, validation, status handling, and Dashboard entry point.

### Modified Capabilities

- None.

## Impact

- Backend: `BankAccount` service/API/MVC layers, DTOs, validation, and focused tests.
- Frontend: new banking account template, JavaScript API/page modules, styles, localized messages, and Dashboard capability registry/tests.
- No new dependency, database migration, external asset, or external network request is required.
