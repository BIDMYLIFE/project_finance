## Purpose

Automatically assigns a unique, human-readable quote number to every new quote at creation time, eliminating NULL quote numbers that violate the database unique constraint.

## ADDED Requirements

### Requirement: Quote number generated at creation

The system SHALL generate a unique quote number for every new quote when it is created. The quote number SHALL be non-null and persisted to the database.

#### Scenario: New quote receives a number

- **WHEN** a user creates a new quote via the API
- **THEN** the quote is persisted with a non-null `quoteNumber` field

#### Scenario: Quote number is unique within the organization

- **WHEN** two quotes are created sequentially within the same organization
- **THEN** each quote SHALL have a distinct `quoteNumber`

### Requirement: Quote number format

The quote number SHALL follow the format `Q-XXXXXXXX` where `XXXXXXXX` is 8 lowercase hexadecimal characters derived from the quote's UUID.

#### Scenario: Format conformance

- **WHEN** a quote with UUID `4e6da3b0-220f-4055-9f31-647285f03a90` is created
- **THEN** the quote number is `Q-4e6da3b0`

### Requirement: Existing quotes unaffected

Quotes created before this change that have a NULL `quoteNumber` SHALL continue to exist and function normally.

#### Scenario: Legacy quotes with null number

- **WHEN** a quote created before this change has a NULL `quoteNumber`
- **THEN** the quote remains queryable and its status can be transitioned as before
