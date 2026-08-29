## Why

The application defaults to `zh-TW`, but the `/expenses` page currently resolves its localized keys from `messages_zh_TW.properties`, where the Expense entries are still English. This makes the page inconsistent with the rest of the Traditional Chinese experience and should be corrected before the feature is used by Chinese-speaking administrators.

## What Changes

- Translate all `expenses.*` UI messages in the `zh-TW` resource bundle into Traditional Chinese.
- Keep the existing message keys and English resource values so English remains available when explicitly selected.
- Verify the `/expenses` page and its client-side operational messages render the Traditional Chinese bundle under the default locale.

## Capabilities

### New Capabilities

- None.

### Modified Capabilities

- `expense-ui-crud`: Require the Expenses page to present its user-facing text in Traditional Chinese when the active locale is `zh-TW`.

## Impact

- Resource file: `src/main/resources/i18n/messages_zh_TW.properties`.
- Expense page controller/UI tests for locale-specific labels and client-side messages.
- No API, database, authorization, calculation, or dependency changes.
