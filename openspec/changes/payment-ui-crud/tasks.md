## 1. Domain and persistence

- [x] 1.1 Review existing payment, payment category, allocation, receipt print, invoice and bank transaction mappings against the payment-receipts contract.
- [x] 1.2 Add or complete organization-scoped repositories and locked receipt sequence access for payments and categories.
- [ ] 1.3 Add request/response DTOs with Bean Validation for categories, payments, allocations, posting, voiding and receipt output.

## 2. Payment and category services

- [x] 2.1 Implement category list, create, rename and deactivate operations with organization isolation and duplicate-name validation.
- [x] 2.2 Implement payment create/list/detail/confirm operations with currency, category, customer, amount, reason and status validation.
- [x] 2.3 Implement receipt number generation using the organization/year sequence and preserve the number during reprints.
- [ ] 2.4 Implement transactional invoice allocation with currency and remaining-balance checks, including rollback tests.
- [ ] 2.5 Implement pending-deposit posting, voiding, account changes and receipt print audit behavior using existing banking contracts.

## 3. Web API and MVC routes

- [x] 3.1 Add separate Payment API and MVC controllers with consistent pagination, status codes and error DTOs.
- [x] 3.2 Add `/api/v1/payments`, category, allocation, posting, voiding and receipt endpoints with authenticated organization scope.
- [x] 3.3 Add `/payments` page route and supply all required Traditional Chinese messages to the template.

## 4. Offline responsive UI

- [x] 4.1 Add local payment API modules and a Vue 3 payment management page for filters, list, detail and create/confirm actions.
- [ ] 4.2 Add category management, invoice allocation, pending-deposit and void controls with loading, validation, empty and network-error states.
- [ ] 4.3 Add receipt detail and fixed A4 three-copy print layout using local CSS and existing SweetAlert2 interactions.
- [x] 4.4 Verify no CDN or external runtime asset is introduced and validate desktop/mobile rendering contracts.

## 5. Dashboard, tests and verification

- [x] 5.1 Update the capability registry so payments is available at `/payments` and add dashboard navigation assertions.
- [ ] 5.2 Add service, API, repository/transaction, receipt print and UI asset tests covering organization isolation and invalid transitions.
- [x] 5.3 Run formatting, static checks, unit/integration tests, package build and OpenSpec validation.
