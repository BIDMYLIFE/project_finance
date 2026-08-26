## Purpose

Provide administrators with a safe, organization-scoped way to maintain bank account master data used by the ERP's payment and banking workflows.

## ADDED Requirements

### Requirement: Bank accounts are organization-scoped and listable

The system SHALL allow an authenticated administrator to list bank accounts belonging to the current organization with consistent pagination, status filtering, and optional name keyword filtering. The response SHALL expose only account data safe for the management UI and SHALL include identifier, account name, currency code, opening balance, active status, and creation timestamp.

#### Scenario: List active accounts

- **WHEN** an administrator requests the bank account list with the active filter
- **THEN** the system returns only active accounts from the current organization in the standard paginated response format

#### Scenario: Organization isolation

- **WHEN** an administrator requests accounts while another organization's account identifier or data exists
- **THEN** the system does not return or modify the other organization's account data

### Requirement: Administrators can create and update bank accounts

The system SHALL allow an authenticated administrator to create a bank account and update its account name, three-letter currency code, and opening balance. Account names SHALL be required, unique within the organization without case sensitivity, and limited to 200 characters. Currency codes SHALL be required uppercase ISO-style three-letter codes, and opening balances SHALL be valid monetary values with at most four decimal places.

#### Scenario: Create a valid account

- **WHEN** an administrator submits a unique account name, valid currency code, and valid opening balance
- **THEN** the system creates an active account in the current organization and returns its safe response with HTTP 201

#### Scenario: Reject invalid or duplicate account data

- **WHEN** an administrator submits missing, malformed, out-of-range, or organization-duplicate account data
- **THEN** the system returns the standard validation or business error response and does not create or partially update an account

#### Scenario: Update an existing account

- **WHEN** an administrator submits valid editable fields for an account in the current organization
- **THEN** the system updates that account and returns the updated safe response

### Requirement: Deactivation preserves bank account history

The system SHALL deactivate an active bank account through the management API and SHALL preserve the record for historical references. Deactivated accounts SHALL remain queryable when inactive records are requested but SHALL not be offered as active choices to new payment or banking operations.

#### Scenario: Deactivate an account

- **WHEN** an administrator confirms deactivation of an active account
- **THEN** the system marks it inactive, returns HTTP 204, and does not delete the record

#### Scenario: Deactivate an already inactive account

- **WHEN** an administrator requests deactivation for an inactive account
- **THEN** the system leaves the record unchanged and returns the standard idempotent success response

### Requirement: Bank account management has a responsive offline UI and Dashboard entry point

The system SHALL provide a responsive management page at `/banking` with localized labels, loading, empty, validation, network-error, success, create, edit, and deactivate states. The Dashboard SHALL expose banking as an available navigation link to `/banking`. The page SHALL use only packaged local resources and SHALL preserve keyboard-accessible forms and controls.

#### Scenario: Open banking from Dashboard

- **WHEN** an authenticated user selects the available Banking item on the Dashboard
- **THEN** the browser navigates to `/banking` without requesting an undefined route

#### Scenario: Maintain an account from a narrow viewport

- **WHEN** an administrator opens the banking page on a mobile-width viewport
- **THEN** filters, account results, form controls, action buttons, and feedback remain readable and usable without horizontal overflow

#### Scenario: Operate without external assets

- **WHEN** external network access is blocked while the banking page is opened
- **THEN** the page, its controls, and its CRUD behavior use packaged application assets and the application API only
