## 1. Database and Domain Model

- [x] 1.1 Add a versioned SQL Server migration for `SOUTHWND.expense_categories` with UUID primary key, organization foreign key, name, active flag, created timestamp, organization-scoped unique name constraint, and bounded-query index.
- [x] 1.2 Add the `ExpenseCategory` entity and map all persisted fields, including the active state and organization scope.
- [x] 1.3 Add organization-scoped repository queries for lookup by ID, duplicate-name detection, filtered listing, and active-category validation.

## 2. Application Services and API

- [x] 2.1 Add create, update, list, and deactivate DTOs/responses with Bean Validation and the existing pagination/sorting contract.
- [x] 2.2 Implement the ExpenseCategory Service with trim/validation, organization isolation, duplicate-name handling, active-state rules, and safe deactivation.
- [x] 2.3 Implement the protected Web API Controller for expense-category CRUD/list/deactivate operations, returning the existing success and centralized error response structures.
- [x] 2.4 Ensure category lookup exposed for future Expense workflows rejects inactive or cross-organization categories without exposing internal exception details.

## 3. Verification

- [x] 3.1 Add repository/integration tests for schema constraints, organization-scoped uniqueness, listing filters, sorting, and bounded pagination.
- [x] 3.2 Add service tests for trimming, invalid names, duplicate create/update, update behavior, deactivation, and preservation of existing references.
- [x] 3.3 Add API/security tests for unauthenticated and non-ADMIN access, cross-organization IDs, consistent validation errors, and successful CRUD/deactivate responses.
- [x] 3.4 Run the project formatter, relevant test suites, OpenSpec validation, and full build; confirm no application code references external CDN assets or secrets.
