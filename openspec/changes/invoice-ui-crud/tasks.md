## 1. Invoice domain and persistence

- [x] 1.1 Add `Invoice` and `InvoiceLine` JPA entities mapped to the existing organization-scoped tables, including status, dates, totals, paid/balance fields, source quote reference, and immutable line snapshot fields.
- [x] 1.2 Add organization-scoped Invoice, InvoiceLine, and document-sequence repository queries for bounded list/detail access and issue-number allocation.
- [x] 1.3 Add invoice request/response and line DTOs with Bean Validation for customer, dates, currency, quantities, discounts, tax rates, and supported money precision.
- [x] 1.4 Add fixed-precision invoice calculation and response mapping tests covering line totals, tax totals, grand totals, balance due, and product snapshot preservation.

## 2. Invoice service and API

- [x] 2.1 Implement `InvoiceService` create/update/list/detail operations with authenticated organization scope, active customer/product validation, line snapshots, and atomic persistence.
- [x] 2.2 Implement draft-only editing, issue/cancel transitions, overdue status evaluation, and rejection of invalid lifecycle operations without hard deletion.
- [x] 2.3 Implement organization/year invoice sequence locking and unique, non-reusable invoice number generation in the issue transaction.
- [x] 2.4 Add Invoice Web API endpoints for paginated list, detail, draft create/update, issue, and cancel with consistent DTOs, HTTP statuses, validation, and centralized errors.
- [x] 2.5 Add service/API tests for invalid references, negative or inconsistent totals, lifecycle transitions, overdue status, sequence uniqueness/concurrency, cross-organization isolation, and safe responses.

## 3. Invoice MVC and offline UI

- [x] 3.1 Add the protected MVC `/invoices` controller and all English/Traditional Chinese message keys required for headings, filters, table, detail, form, line items, statuses, errors, confirmations, and pagination.
- [x] 3.2 Add local invoice API/page modules and styles using the existing Axios, Vue, Bootstrap, SweetAlert2, loading/error/empty, validation, and responsive patterns.
- [x] 3.3 Add the responsive `/invoices` template with customer/date/currency fields, editable line items, calculated totals, draft create/edit flow, detail display, issue/cancel actions, and accessible controls.
- [x] 3.4 Add frontend resource, i18n, offline asset, and workflow tests covering draft editing, line validation, status actions, detail rendering, local-only resources, and narrow viewport layout.

## 4. Dashboard integration and delivery verification

- [x] 4.1 Enable the Dashboard invoices capability with route `/invoices` and preserve all other capability ownership, routes, and unavailable semantics.
- [x] 4.2 Add Dashboard and MVC security/navigation tests proving the invoice link resolves to the protected `/invoices` page and undefined routes remain unavailable.
- [x] 4.3 Run full Maven tests, package build, OpenSpec validation, and available browser/mobile checks; document any remaining tax-jurisdiction, payment-allocation, or browser-specific risks.
