## 1. Preconditions and Source Contracts

- [x] 1.1 Verify that `auth-jwt-admin-bootstrap` provides authenticated `ADMIN` principal, organization context, and the shared API error contract
- [x] 1.2 Verify sales-document, payment/banking, expense, and tax source contracts expose organization, status, amount, currency, source id, and required date fields
- [x] 1.3 Confirm report date-basis mapping for `received_at`, `transaction_date`, `issue_date`, `due_date`, `expense_date`, and document date
- [x] 1.4 Confirm the report default date window, maximum page size, maximum CSV export rows, supported sort fields, and timezone behavior

## 2. Shared Report Query Foundation

- [x] 2.1 Define report type, filter request, normalized applied-filter, date-basis, row, summary, pagination, empty-state, and typed-source-reference DTOs
- [x] 2.2 Implement shared validation for date ranges, default/bounded periods, page/size, sort fields, status values, currencies, and applicable filter combinations
- [x] 2.3 Implement authenticated organization-scope resolution that ignores organization ids supplied by query, path, or request body
- [x] 2.4 Implement shared effective-state predicates for cancelled, voided, reversed, posted, and other source status rules
- [ ] 2.5 Implement report query service/repository projections with organization, date-basis, status, customer, category, account, currency, and source indexes
- [x] 2.6 Add empty-result and summary-zero behavior to the common report response contract

## 3. Payment and Banking Reports

- [ ] 3.1 Implement pending-deposit projection using `received_at` with receipt number, payer, category, reason, note, amount, currency, method, and payment date
- [ ] 3.2 Implement payment-category grouping with customer, period, category, currency, valid-count, valid-amount, and traceable rows
- [ ] 3.3 Implement bank-balance projection using `transaction_date`, opening balance, effective Credit/Debit totals, net change, closing balance, and transaction rows
- [x] 3.4 Implement reversal-chain handling so original and reversal bank transactions are not counted twice
- [ ] 3.5 Add payment and banking report tests for date boundaries, category/customer/currency/account filters, pending state, void exclusion, and balance recomputation

## 4. Invoice, Receivable, Expense, and Tax Reports

- [x] 4.1 Implement invoice-status report using issue/status date, customer/currency/status filters, and traceable invoice rows
- [ ] 4.2 Implement receivable-aging report using due date and effective outstanding balance with not-due, 1-30, 31-60, and 61-plus day buckets
- [x] 4.3 Implement expense report using `expense_date`, expense category, payee, account, currency, status, amount, and source rows
- [ ] 4.4 Implement tax report using document date, tax rate, tax amount, currency, source document, and period totals
- [x] 4.5 Implement ERP cash-flow summary using posted payment Credits and valid expense Debits with income, expense, net, and source rows
- [ ] 4.6 Add report tests for paid/cancelled/voided source exclusion, aging boundaries, status totals, tax aggregation, and cash-flow reconciliation

## 5. Source Traceability and Security

- [ ] 5.1 Implement source-detail read routing for invoice, payment, expense, and bank transaction row references without exposing source entities directly
- [ ] 5.2 Reapply authenticated organization scope to every report query, source-detail lookup, summary, and applied-filter response
- [ ] 5.3 Add negative tests for organization ids forged in query, path, body, source reference, and export requests
- [ ] 5.4 Add tests proving report summaries can be recomputed from rows under identical filters, date basis, and effective-state rules
- [ ] 5.5 Verify report responses and logs omit credentials, tokens, unnecessary sensitive fields, stack traces, and other organization identifiers

## 6. Reporting APIs and CSV Export

- [x] 6.1 Implement versioned Web API endpoints for each report type with DTO binding, validation, authorization, consistent response shape, and centralized errors
- [x] 6.2 Keep report Web API Controllers limited to request parsing, service invocation, and DTO response assembly; keep page routing in separate MVC Controllers
- [x] 6.3 Implement CSV generation by reusing the same normalized query specification, effective-state rules, sort, date basis, and filters as the report API
- [x] 6.4 Include CSV metadata for report type, applied filters, date basis, currency, headings, summary, and generation time
- [x] 6.5 Enforce bounded export size and ensure query/serialization failure does not publish a partial or apparently successful file
- [ ] 6.6 Add API and CSV tests for filtered output, empty output, invalid bounds, export consistency with query results, download errors, and organization isolation
- [ ] 6.7 Implement PDF export with fixed localized layout, report metadata, summary, source references, bounded output, and offline-local font/resource handling
- [ ] 6.8 Implement XLSX export with fixed localized worksheets, detail and summary sections, decimal/date/currency formatting, bounded output, and offline build support
- [ ] 6.9 Add PDF/XLSX tests for file readability, metadata, filtered consistency, empty output, limit errors, failure cleanup, and organization isolation

## 7. Offline Responsive Report Experience

- [ ] 7.1 Add local/build-provided Vue 3, Bootstrap 5.3, Axios, SweetAlert2, font, and icon assets and verify no CDN or external runtime dependency
- [ ] 7.2 Implement shared Axios report services with centralized URLs, timeout, authentication failure, validation, network, loading, success, empty, retry, and disabled states
- [ ] 7.3 Implement responsive report navigation, filter controls, tables, summaries, source links, and CSV actions for desktop and mobile viewports
- [ ] 7.4 Provide visible keyboard focus and accessible names for filters, pagination, row actions, source navigation, and export controls
- [ ] 7.5 Ensure report tables and filters remain readable without overlap or hidden content at supported viewport sizes
- [ ] 7.6 Add browser tests for report loading/error/empty states, filters, pagination, source navigation, CSV download, keyboard operation, offline assets, and mobile layout
- [ ] 7.7 Implement Dashboard report entry, core summary cards, loading/error/empty states, and links that preserve validated report filters
- [ ] 7.8 Add Dashboard integration tests proving capability availability, summary consistency with report responses, authorization, and organization isolation

## 8. End-to-End Verification and Delivery

- [ ] 8.1 Run source-contract, unit, integration, API, security-boundary, report-summary, and CSV tests with at least two organizations
- [ ] 8.2 Verify all documented date bases, timezone boundaries, default periods, pagination limits, export limits, and sort behavior
- [ ] 8.3 Verify bank reversals, payment voids, invoice cancellations, and effective-state exclusions do not duplicate report totals
- [ ] 8.4 Run formatting, static checks, test suites, browser tests, and the full build; resolve only failures introduced by this change
- [ ] 8.5 Document source capability prerequisites, report endpoint contract, date-basis rules, export limits, offline startup, deployment order, and rollback procedure
- [ ] 8.6 Verify CSV, PDF, and XLSX output consistency against the same normalized query specification and summary
- [ ] 8.7 Verify Dashboard summaries and navigation against report APIs across supported currencies, date boundaries, statuses, and empty data
- [ ] 8.8 Document PDF/XLSX dependencies, localized formatting, file limits, offline operation, and recovery from failed export generation
