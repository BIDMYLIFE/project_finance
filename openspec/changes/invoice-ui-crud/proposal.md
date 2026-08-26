## Why

The database schema and invoice status vocabulary exist, but the ERP currently has no invoice domain model, API, page, or Dashboard entry point. Users therefore cannot turn validated sales information into traceable invoice drafts or issued documents, nor review receivable status through the application.

## What Changes

- Add organization-scoped invoice and invoice-line domain handling with customer/product snapshot data and fixed-precision totals.
- Add invoice draft create, edit, list, detail, issue, and cancel operations with lifecycle validation; preserve issued history instead of hard deletion.
- Add invoice numbering, due-date/overdue presentation, paid total and balance due fields needed by later payment allocation.
- Add REST DTOs, validation, service rules, API endpoints, MVC routing, localized responsive Vue UI, and focused tests.
- Add `/invoices` as an available Dashboard navigation link while preserving existing capability routes and authentication boundaries.
- Use only packaged local frontend assets; do not add external integrations, tax-jurisdiction-specific invoice printing, or payment allocation execution in this change.

## Capabilities

### New Capabilities

- `invoice-ui-crud`: Organization-scoped invoice draft and lifecycle management, detail/list UI, totals, status, numbering, and Dashboard entry point.

### Modified Capabilities

- None.

## Impact

- Backend: Invoice/InvoiceLine entities, repositories, DTOs, service, API/MVC controllers, invoice sequence handling, and tests.
- Frontend: invoice template, API/page modules, styles, localized messages, local asset checks, and Dashboard capability registry/tests.
- Integrates with existing Customer and Product master data and the existing payment-allocation schema without implementing payment workflows.
- No new dependency or database migration is expected; existing invoice tables and `InvoiceStatus` are used.
