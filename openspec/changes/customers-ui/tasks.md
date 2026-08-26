## 1. Page Entry and API Contract

- [x] 1.1 Add an authenticated MVC page controller for `/customers` that returns the customers Thymeleaf view without changing the existing API controller.
- [x] 1.2 Confirm the existing customer request/response and page response fields used by the UI, including keyword, active status, page number, page size, total elements, and total pages.
- [x] 1.3 Add focused controller/API contract tests proving `/customers` is separate from `/api/v1/customers` and the existing customer CRUD endpoints remain compatible.

## 2. Offline Page Resources

- [x] 2.1 Create the `templates/customers/` Thymeleaf page with local Vue 3, Axios, Bootstrap 5.3, and SweetAlert2 references only.
- [x] 2.2 Add the customers page JavaScript module and API helper using the project’s existing client/configuration conventions.
- [x] 2.3 Add responsive customers page CSS that keeps the table, filters, form, actions, and pagination usable without overlap at desktop and narrow viewport sizes.

## 3. Customer List Workflow

- [x] 3.1 Implement the default active-customer list request and render customer rows with server-provided pagination metadata.
- [x] 3.2 Implement name keyword search and active/inactive filtering, resetting the server page to the first page whenever criteria change.
- [x] 3.3 Implement server-side previous, next, and page-number navigation without client-side reconstruction of unloaded results.
- [x] 3.4 Implement loading, API failure with retry, and successful empty-result states while preventing duplicate requests.

## 4. Customer Maintenance Workflow

- [x] 4.1 Implement a reusable create/edit form with client-side required-field and format validation that preserves invalid input and avoids invalid requests.
- [x] 4.2 Implement create and update requests, success feedback, form reset/close behavior, and refresh of the current query.
- [x] 4.3 Implement soft deactivation with localized SweetAlert2 confirmation, ensuring cancellation makes no DELETE request and confirmation refreshes the list.
- [x] 4.4 Map validation responses to safe localized field/form messages and map other failures to localized messages without exposing internal exception details or tokens.

## 5. Localization, Access, and Verification

- [x] 5.1 Add and verify all customers page Chinese and English message keys for labels, actions, statuses, validation, confirmation, loading, empty, success, failure, and retry states.
- [x] 5.2 Verify unauthenticated access follows the existing login protection flow and never renders customer data.
- [x] 5.3 Add or update automated tests for list criteria, pagination, create, update, deactivation confirmation/cancellation, validation, error states, and locale rendering.
- [x] 5.4 Run project formatting, static checks, tests, and build; manually verify offline resource loading and RWD behavior at desktop and mobile viewport sizes.
- [x] 5.5 Record favicon 500 as a non-blocking follow-up risk only; do not modify favicon handling within this change.

Follow-up risk: favicon requests returning HTTP 500 remain outside this change and are intentionally not modified.
