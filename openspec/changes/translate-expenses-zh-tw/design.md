## Context

The locale resolver defaults to `zh-TW`, and the MVC controller resolves the Expense message keys using the active `Locale`. The `messages.properties` fallback entries are already Traditional Chinese, while the `messages_zh_TW.properties` Expense block is English.

## Goals / Non-Goals

**Goals:**

- Make the `zh-TW` Expense resource bundle consistently Traditional Chinese.
- Cover both server-rendered labels and JavaScript messages because the page injects the controller's resolved message map into the browser.
- Keep message keys stable and preserve English values for the base/English resource behavior.

**Non-Goals:**

- Adding a language switcher or changing locale resolution.
- Translating other modules whose resource bundles may have the same broader inconsistency.
- Changing API error codes or business-rule messages returned by the backend.

## Decisions

- Update only the `expenses.*` values in `messages_zh_TW.properties`. This is the resource actually selected by the configured default locale and limits the change to the requested page.
- Reuse the existing keys rather than adding parallel Chinese keys. This keeps `ExpensesPageController` and `MSG(...)` calls unchanged.
- Add resource-level assertions for representative page, form, lifecycle, and error messages, plus a controller/UI test if the existing test harness supports locale injection.

## Risks / Trade-offs

- [Risk] Some fallback text in the HTML remains Chinese or English in source markup. → Normal Thymeleaf rendering replaces keyed text; tests should focus on resolved resource values and key coverage.
- [Risk] Other pages may still have English values in the `zh-TW` bundle. → Keep scope limited to `/expenses` and record broader bundle cleanup as separate work.
