## Why

Creating a quote fails with a UNIQUE KEY constraint violation on `uq_quotes_number` because the service saves `null` for `quote_number`. SQL Server treats multiple `(org_id, NULL)` rows as duplicates under a UNIQUE constraint. Every second draft quote insertion crashes.

## What Changes

- `QuoteService.create()` now generates a unique quote number from the UUID before persisting, instead of saving `null`.
- Format: `"Q-" + first 8 hex characters of UUID` (e.g. `Q-4e6da3b0`).
- No schema or migration change required — the column is already `NVARCHAR(40) NULL` and the unique constraint is satisfied by UUID uniqueness.

## Capabilities

### New Capabilities

- `quote-number-generation`: Automatic generation of unique quote numbers from UUID at creation time.

### Modified Capabilities

(none)

## Impact

- **Code**: `QuoteService.create()` — single line change.
- **API**: `QuoteResponse.quoteNumber` will now always be non-null for new quotes.
- **Database**: No changes.
- **No breaking change**: existing quotes with `null` numbers continue to work; the change only affects new creations.
