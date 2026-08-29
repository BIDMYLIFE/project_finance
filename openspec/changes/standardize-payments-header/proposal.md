## Why

The `/payments` page already provides a Dashboard button, but its custom header differs from the shared management-page navigation used by Expenses, Quotes, Invoices, and Banking. It also omits the logout control and uses a different Dashboard translation, creating an inconsistent navigation experience for administrators.

## What Changes

- Replace the Payments custom header with the shared responsive navigation pattern.
- Keep a localized Dashboard/工作台 link to `/` and add the localized logout action with loading/disabled state.
- Align Payments navigation markup and styling with the existing management pages without changing payment workflows or APIs.
- Add regression checks for the header links, localization keys, logout action, and responsive navigation structure.

## Capabilities

### New Capabilities

- None.

### Modified Capabilities

- `payment-receipts`: Require the payment management UI to provide the same authenticated Dashboard and logout navigation pattern as other management pages.

## Impact

- Payment page template and CSS/JavaScript navigation markup.
- Payment MVC message keys and localized Dashboard label.
- Payment UI asset tests and responsive navigation checks.
- No API, database, payment calculation, authorization, or third-party dependency changes.
