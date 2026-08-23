## 1. Foundation and Decisions

- [ ] 1.1 Verify that `auth-jwt-admin-bootstrap` provides the authenticated `ADMIN` principal, organization context, cookie/API error contract, and protected-route integration required by this change
- [ ] 1.2 Confirm the target tax jurisdiction fields, receipt/invoice branding and paper margins, supported currencies, date/timezone policy, and long-note print behavior before implementing affected views
- [ ] 1.3 Confirm the SQL Server migration and integration-test strategy without committing real connection strings or credentials
- [ ] 1.4 Add application dependencies and package structure for Spring Boot layers, SQL Server persistence, migrations, Vue 3, local Bootstrap 5.3, Axios, and SweetAlert2
- [ ] 1.5 Implement shared DTO validation, API error response mapping, pagination/sorting parameters, organization context access, and audit actor resolution
- [ ] 1.6 Implement fixed-precision money, quantity, tax, currency-scale, and rounding policies as shared domain services

## 2. Database Schema and Shared Domain

- [ ] 2.1 Create migrations for organizations, customers, products, payment categories, bank accounts, audit logs, and document/receipt sequences
- [ ] 2.2 Create migrations for quotes, quote lines, invoices, invoice lines, payments, payment allocations, receipt print records, expenses, and bank transactions
- [ ] 2.3 Add foreign keys, organization-scoped unique constraints, status/active constraints, source references, reversal references, and bounded-query indexes
- [ ] 2.4 Add entity mappings and enums for master data, sales documents, payments, receipts, expenses, banks, audit records, and append-only transaction status
- [ ] 2.5 Add repository interfaces and organization-scoped query methods for all shared domain aggregates

## 3. Customer and Product Master Data

- [ ] 3.1 Implement customer create, update, query, pagination, and deactivate services with organization-scoped uniqueness and active-state rules
- [ ] 3.2 Implement product/service create, update, query, pagination, and deactivate services with price, currency, tax, and active-state validation
- [ ] 3.3 Implement master-data Web API controllers with DTO responses, filtering, sorting, bounded page sizes, and centralized errors
- [ ] 3.4 Add unit and integration tests for duplicate identifiers, invalid prices/currencies, inactive records, and cross-organization access

## 4. Quotes and Invoices

- [ ] 4.1 Implement quote draft creation with active customer/product validation, line snapshots, tax calculation, currency policy, and validity dates
- [ ] 4.2 Implement the quote state machine for `DRAFT`, `SENT`, `ACCEPTED`, `REJECTED`, `EXPIRED`, and `CANCELLED`, including invalid-transition errors
- [ ] 4.3 Implement accepted-quote conversion to an invoice draft while preserving source quote, customer, line, currency, and tax-result references
- [ ] 4.4 Implement invoice draft creation and line snapshots so issued documents do not change when product master data changes
- [ ] 4.5 Implement organization/year invoice sequence locking, unique invoice numbers, issue transaction, and non-reusable numbering behavior
- [ ] 4.6 Implement invoice state transitions, due-date overdue evaluation, cancellation rules, paid totals, and balance-due updates
- [ ] 4.7 Implement invoice/payment allocation validation so total allocations cannot exceed payment amount or invoice outstanding balance
- [ ] 4.8 Add quote/invoice API controllers, DTOs, list filters, detail responses, and source-document traceability
- [ ] 4.9 Add calculation, sequence-concurrency, state-transition, conversion, allocation, overdue, and organization-isolation tests

## 5. Payments, Receipts, and Suggestions

- [ ] 5.1 Implement organization-scoped payment categories with active-state management and seed/configuration support for initial categories
- [ ] 5.2 Implement payment draft/confirmation validation requiring exactly one active category, non-empty reason, valid amount/date/method, and optional note
- [ ] 5.3 Implement organization/year receipt sequence locking and immutable receipt-number creation in the payment confirmation transaction
- [ ] 5.4 Implement multi-invoice payment allocation and remaining unallocated amount tracking with atomic rollback on validation failure
- [ ] 5.5 Implement `PENDING_DEPOSIT`, `POSTED`, and `VOIDED` payment transitions without deleting payments or reusing receipt numbers
- [ ] 5.6 Implement organization-scoped reason/note autocomplete queries, field-type filtering, active filtering, bounded results, and cache invalidation
- [ ] 5.7 Implement receipt print records and fixed A4 portrait three-copy output using the saved payment snapshot and one shared receipt number
- [ ] 5.8 Implement receipt reprint behavior and long-note layout limits so reprints never create a second payment or a second receipt number
- [ ] 5.9 Implement payment confirmation, allocation, receipt, autocomplete, reprint, void, and organization-isolation Web APIs
- [ ] 5.10 Add payment, receipt sequence, allocation rollback, suggestion isolation, void-history, and reprint tests

## 6. Banking and Expenses

- [ ] 6.1 Implement organization-scoped bank-account services with active-state, currency-compatibility, opening-balance, and deactivation rules
- [ ] 6.2 Implement append-only `CREDIT`/`DEBIT` bank transaction persistence, source references, reversal references, status, and balance query projections
- [ ] 6.3 Implement pending-deposit posting as one transaction that creates a bank Credit and changes the payment to `POSTED`
- [ ] 6.4 Implement expense create/confirm/void services and post confirmed expenses as auditable bank Debits
- [ ] 6.5 Implement account-to-account transfers as atomic source Debit and destination Credit pairs with shared transfer references
- [ ] 6.6 Implement posted-payment account changes and financial corrections using reversal/void chains plus a new valid Credit without overwriting history
- [ ] 6.7 Implement bank-account, expense, deposit, transfer, correction, and balance Web APIs with organization scope and centralized errors
- [ ] 6.8 Add tests for inactive/foreign accounts, balance recomputation, posting rollback, transfer atomicity, reversal chains, and cross-organization isolation

## 7. ERP Reporting and Export

- [ ] 7.1 Implement bounded report query specifications with shared organization, date, customer, category, bank-account, currency, status, sort, and pagination filters
- [ ] 7.2 Implement pending-deposit and payment-category reports using `received_at`, receipt fields, valid-state totals, and traceable source rows
- [ ] 7.3 Implement bank-balance, invoice-status, receivable-aging, expense, tax, and ERP cash-flow summary queries with their documented date bases
- [ ] 7.4 Implement exclusion and reversal rules so voided, cancelled, and reversed records do not duplicate effective totals
- [ ] 7.5 Implement report detail traceability to source invoices, payments, expenses, and bank transactions within the current organization
- [ ] 7.6 Implement CSV export by reusing report query specifications and including filters, date basis, currency, headings, totals, and generation time
- [ ] 7.7 Implement report Web APIs with consistent rows, summary, pagination, applied-filters, empty-state, and export responses
- [ ] 7.8 Add report tests comparing summaries with source records, validating aging buckets, CSV consistency, bounded queries, and organization isolation

## 8. Offline Web Experience

- [ ] 8.1 Add local vendor/build assets for Vue 3, Bootstrap 5.3, Axios, SweetAlert2, fonts, and icons with no CDN or external runtime dependency
- [ ] 8.2 Implement shared Vue API services and state handling for loading, success, validation error, network error, empty data, disabled, and retry states
- [ ] 8.3 Implement responsive, keyboard-accessible customer, product, quote, invoice, payment, bank, expense, and report pages using the API contracts
- [ ] 8.4 Implement autocomplete form controls that retain editable selected text and distinguish reason from note suggestions
- [ ] 8.5 Implement receipt print view and print stylesheet with A4 portrait sizing, three fixed copies, page-break rules, and browser print-preview verification
- [ ] 8.6 Implement CSV download feedback and report filters so the exported dataset matches the visible query
- [ ] 8.7 Add browser tests for core workflows at desktop and mobile viewports, including no-overlap, keyboard focus, empty/error states, CSV download, and one-page receipt printing

## 9. End-to-End Verification and Delivery

- [ ] 9.1 Run migrations against a clean SQL Server test database and verify rollback behavior for failed cross-aggregate transactions
- [ ] 9.2 Run unit, integration, API, security-boundary, concurrency, browser, report, CSV, and print tests with at least two organizations
- [ ] 9.3 Verify no API response, log, fixture, CSV, frontend asset, or documentation contains real passwords, JWT secrets, database credentials, or stack traces
- [ ] 9.4 Run formatting, static checks, test suites, and the full build; resolve only failures introduced by this change
- [ ] 9.5 Document deployment order, required authentication prerequisite, SQL Server migration procedure, offline startup, backup/dry-run steps, and known non-goals