## 1. Page Entry and Existing Contract

- [x] 1.1 Add an authenticated, dedicated Thymeleaf MVC page controller for `/products` that returns the products view and remains separate from `MasterDataApiController`.
- [x] 1.2 Confirm the existing Product entity, ProductRequest, ProductResponse, page response, organization scope, ADMIN authorization, and query parameter mappings used by the UI.
- [x] 1.3 Add MVC/API contract tests proving `/products` is a page route, existing GET/POST/PUT/DELETE `/api/v1/products` (including `{id}`) remain compatible, and no database migration or new API contract is introduced.

## 2. Offline Page Resources and Localization

- [x] 2.1 Create the `templates/products/` Thymeleaf page using only project-local Vue 3, Axios, Bootstrap 5.3, SweetAlert2, CSS, and other required static/webjar resources; verify there are no CDN references.
- [x] 2.2 Add the products page JavaScript module and API helper using the existing customers UI and API client conventions, with centralized URL and request configuration.
- [x] 2.3 Add classical-style responsive products CSS with stable table, form, action, and pagination dimensions that remain usable without overlap on desktop and narrow viewports.
- [x] 2.4 Add matching Chinese and English message keys for labels, actions, statuses, validation, confirmation, loading, empty, failure, retry, and success states, then verify locale rendering has no missing keys.

## 3. Product and Service List Workflow

- [x] 3.1 Implement the default products API request and render Product fields including product code, name, unit price, currency, tax rate, and active status without adding a type field or product/service switch.
- [x] 3.2 Implement name search and active/inactive filtering through server query parameters, resetting to page one whenever criteria change.
- [x] 3.3 Implement server-side previous, next, and page-number navigation from API pagination metadata without reconstructing unloaded results in the browser.
- [x] 3.4 Implement loading, successful empty-result, API failure, and retry states while preventing duplicate or stale list requests.

## 4. Product Maintenance Workflow

- [x] 4.1 Implement a shared create/edit form for `productCode`, `name`, `description`, `unitPrice`, `currencyCode`, `taxRate`, and existing `active` behavior; make product code read-only in edit mode.
- [x] 4.2 Add client-side validation for required fields, maximum lengths (80/200/1000), non-negative unit price, supported currencies (TWD/USD/EUR/JPY), and tax rate from 0 through 100; preserve invalid input and avoid invalid API requests.
- [x] 4.3 Implement create with the existing products POST API, localized success feedback, form reset/close, and refresh of the current query.
- [x] 4.4 Implement edit with the existing products PUT API, localized success feedback, form reset/close, and refresh of the current query.
- [x] 4.5 Implement soft deactivation with localized SweetAlert2 confirmation and the existing DELETE `{id}` API; ensure cancellation sends no request and confirmation refreshes the list without physical deletion.
- [x] 4.6 Map existing API validation responses to safe localized field/form messages and map all other failures to generic localized messages without exposing stack traces, internal errors, database details, or tokens.

## 5. Access, Usability, and Workflow Verification

- [x] 5.1 Verify unauthenticated page/API access follows the existing login, organization scope, and ADMIN authorization flow and never renders unauthorized product data.
- [x] 5.2 Add workflow tests for search, active/inactive filtering, server pagination, loading/error/retry/empty states, duplicate-request prevention, create, edit, validation, deactivation confirmation/cancellation, and locale rendering.
- [x] 5.3 Verify keyboard navigation, visible focus states, disabled/loading states, responsive layout, readable table/form controls, and non-overlapping interactions at desktop and mobile viewport sizes.
- [x] 5.4 Run project formatting, static checks, unit/integration/workflow tests, and build; verify offline resource loading and confirm no source/API/database changes outside the planned products UI scope.
- [x] 5.5 Record favicon HTTP 500 only as a non-blocking follow-up risk and leave favicon handling unchanged in this change.

Follow-up risk: favicon HTTP 500 remains out of scope and favicon handling is unchanged.