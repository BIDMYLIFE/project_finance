## 1. Quote Domain Foundation

- [x] 1.1 Add Quote and QuoteLine JPA mappings, including organization scope, customer/product references, status, dates, monetary fields, and line snapshots, without changing the existing schema contract.
- [x] 1.2 Add organization-scoped quote and quote-line repository interfaces for bounded list queries, detail loading, and lifecycle updates.
- [x] 1.3 Add quote request/response DTOs and nested line validation for customer, product, quantity, discount, currency, tax, and valid-until fields.

## 2. Quote Service and API

- [x] 2.1 Implement server-side quote calculation with BigDecimal, fixed scale, rounding, line totals, subtotal, tax total, and grand total.
- [x] 2.2 Implement draft creation and draft update with active customer/product validation, organization isolation, line snapshots, and atomic persistence.
- [x] 2.3 Implement organization-scoped quote list and detail services with keyword, status, bounded pagination, and query-time expired-status evaluation.
- [x] 2.4 Implement the centralized quote lifecycle transition rules for submit, accept, reject, expire, and cancel, including invalid-transition errors and draft-only editing.
- [x] 2.5 Add Quote Web API endpoints for list, detail, create, draft update, and explicit submit/accept/reject/cancel commands using the existing error response contract.
- [x] 2.6 Add unit and integration tests for totals, invalid input, inactive/foreign references, organization isolation, draft immutability, expiry, legal transitions, and illegal transitions.

## 3. Quotes Page Backend and Frontend

- [x] 3.1 Add the authenticated `/quotes` MVC page controller and localized message collection using the existing customers/products page pattern.
- [x] 3.2 Add the quotes Axios API module for list, detail, create, update, and lifecycle command requests.
- [x] 3.3 Add the Vue quotes page module with list filters, pagination, draft form state, repeatable line editor, validation mapping, loading/error/empty/retry states, and lifecycle feedback.
- [x] 3.4 Add the responsive quotes Thymeleaf template with customer/product selectors, quote lines, totals, status badges, state-dependent actions, accessible confirmation dialogs, and keyboard-friendly controls.
- [x] 3.5 Add quotes CSS and localized Chinese/English messages while keeping all runtime assets local and consistent with the existing visual language.

## 4. Dashboard Integration and Verification

- [x] 4.1 Update the capability registry so quotes uses `/quotes` and `available: true`, allowing the existing Dashboard navigation to render an active link.
- [x] 4.2 Add page, API, and Dashboard navigation tests covering route availability, authenticated access, CRUD flow, lifecycle actions, validation/error states, and local resource references.
- [x] 4.3 Run focused quote tests followed by the existing Maven test/build checks and verify the page at desktop and mobile widths without layout overlap.
