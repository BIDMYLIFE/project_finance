## 1. Preconditions and Shared Contracts

- [ ] 1.1 Verify that `auth-jwt-admin-bootstrap` provides authenticated `ADMIN` principal, organization context, protected-route behavior, and the shared API error contract
- [ ] 1.2 Verify the sales-document invoice contract exposes organization, currency, outstanding balance, and locking behavior required for payment allocation
- [ ] 1.3 Confirm the receipt-year rule, organization timezone, supported currencies, receipt branding, paper margins, three-copy labels, and long-note print limit
- [ ] 1.4 Define the shared DTO validation, pagination limits, money scale/rounding policy, status enums, audit actor fields, and conflict/error codes
- [ ] 1.5 Implement organization context resolution and shared authorization helpers without trusting organization ids from request bodies

## 2. Database Schema and Persistence

- [ ] 2.1 Create SQL Server migrations for organization-scoped payment categories, payments, payment allocations, receipt sequences, and receipt print records
- [ ] 2.2 Create SQL Server migrations for bank accounts and append-only bank transactions with source, reversal, status, actor, currency, and date fields
- [ ] 2.3 Add foreign keys, organization-scoped unique constraints, positive-amount and enum constraints, receipt sequence constraints, and source/reversal indexes
- [ ] 2.4 Add bounded-query indexes for organization, status, received/transaction dates, account, currency, category, and invoice allocation lookups
- [ ] 2.5 Add entity mappings and repository queries that always constrain payment, receipt, category, account, transaction, and allocation access by organization
- [ ] 2.6 Verify clean-database migration and rollback behavior without destructive deletion of financial history

## 3. Payment and Category Services

- [ ] 3.1 Implement organization-scoped payment-category create, list, update, deactivate, and active-state validation services
- [ ] 3.2 Implement payment creation/confirmation validation for payer, date, positive amount, currency, method, exactly one active category, reason, and optional note
- [ ] 3.3 Implement the payment state machine for `PENDING_DEPOSIT`, `POSTED`, and `VOIDED`, including legal transition and repeated-operation conflict rules
- [ ] 3.4 Implement payment DTOs and service responses containing final text snapshots, allocation totals, deposit state, receipt number, and source references
- [ ] 3.5 Add unit tests for category isolation, inactive categories, missing/duplicate categories, blank reasons, invalid amounts, currencies, methods, and state conflicts

## 4. Receipt and Invoice Allocation

- [ ] 4.1 Implement organization/year receipt sequence locking and non-reusable receipt-number creation within payment confirmation transaction
- [ ] 4.2 Implement multi-invoice allocation validation for same-organization invoices, currency compatibility, outstanding balances, payment amount, and atomic rollback
- [ ] 4.3 Implement allocation query responses with per-invoice amount, allocated total, and remaining unallocated amount
- [ ] 4.4 Implement receipt snapshot generation from confirmed payment data without depending on later master-data edits
- [ ] 4.5 Implement receipt print records, original-number reprint behavior, and audit metadata for initial print and reprints
- [ ] 4.6 Add tests for concurrent receipt confirmation, sequence uniqueness, allocation overrun, cross-organization invoice rejection, rollback, and reprint idempotency

## 5. Banking and Posting Services

- [ ] 5.1 Implement organization-scoped bank-account create, list, update, deactivate, opening-balance, and currency validation services
- [ ] 5.2 Implement append-only `CREDIT`/`DEBIT` transaction creation with positive amount, source reference, status, transaction date, actor, and currency validation
- [ ] 5.3 Implement bank-balance projection/query from opening balance plus valid Credit total minus valid Debit total
- [ ] 5.4 Implement payment confirmation into an active compatible account as one transaction that creates the source `CREDIT` and returns `POSTED`
- [ ] 5.5 Implement pending-deposit posting with payment/account locking, duplicate-submit protection, source linkage, and all-or-nothing rollback
- [ ] 5.6 Implement posted-payment account changes through original transaction reversal/void plus new-account `CREDIT`, preserving payment and receipt data
- [ ] 5.7 Implement bank-transaction correction through append-only reversal chains, reason/actor capture, and effective-balance exclusion rules
- [ ] 5.8 Add tests for inactive/foreign/currency-mismatched accounts, balance recomputation, posting rollback, duplicate posting, reversal chains, and transaction immutability

## 6. API and MVC Boundaries

- [ ] 6.1 Implement versioned Web API endpoints for payment categories, payment confirmation, allocation, receipt print/reprint, and payment void
- [ ] 6.2 Implement versioned Web API endpoints for bank accounts, bank transactions, balances, pending-deposit posting, account changes, and corrections
- [ ] 6.3 Keep Web API Controllers limited to request binding, validation, service invocation, and DTO response assembly; keep page routing in separate MVC Controllers
- [ ] 6.4 Apply `ADMIN` authorization, authenticated organization scope, bounded pagination, consistent success responses, and centralized validation/business/authorization errors to every endpoint
- [ ] 6.5 Add API tests for unauthenticated/non-admin access, cross-organization ids, forged organization body fields, not-found behavior, conflict behavior, and no partial writes

## 7. Offline Web Experience and Printing

- [ ] 7.1 Add local/build-provided Vue 3, Bootstrap 5.3, Axios, SweetAlert2, font, and icon assets and verify no CDN or external runtime dependency is introduced
- [ ] 7.2 Implement shared Axios feature services with centralized API URLs, timeout, authentication failure, validation, network, loading, success, empty, retry, and disabled states
- [ ] 7.3 Implement responsive keyboard-accessible payment, pending-deposit, bank-account, transaction, allocation, and receipt screens using API DTOs
- [ ] 7.4 Implement separate editable autocomplete controls for reason and note suggestions without replacing user-edited final text
- [ ] 7.5 Implement A4 portrait one-page three-copy receipt view and print stylesheet using the saved payment snapshot and configured copy labels
- [ ] 7.6 Add browser tests at desktop and mobile sizes for confirmation, allocation, pending posting, reprint, void, validation/error states, keyboard focus, no-overlap, and one-page printing

## 8. End-to-End Verification and Delivery

- [ ] 8.1 Run migration and integration tests with at least two organizations and verify every payment, account, transaction, suggestion, allocation, and API query is isolated
- [ ] 8.2 Run concurrency tests for receipt sequence, simultaneous allocation, duplicate pending posting, account change, and payment void operations
- [ ] 8.3 Verify receipt snapshots, reprint records, reversal chains, effective balances, and payment states remain consistent after rollback and retry
- [ ] 8.4 Verify APIs, logs, fixtures, CSV/document outputs, frontend assets, and documentation contain no credentials, JWT secrets, passwords, or stack traces
- [ ] 8.5 Run formatting, static checks, unit/integration/API/browser tests, and the full build; resolve only failures introduced by this change
- [ ] 8.6 Document deployment order, authentication and invoice prerequisites, migration procedure, rollback limits, offline startup, backup/dry-run, and confirmed non-goals