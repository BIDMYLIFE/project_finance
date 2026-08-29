## Why

The `/quotes` page currently contains a totals preview block before the document structure and outside the Vue mount root. As a result, the browser can display Vue interpolation expressions such as `{{ previewTotals.subtotal }}` literally instead of rendering the calculated quote totals. The existing quote calculation is correct, but the UI placement makes the result unusable.

## What Changes

- Move the quote totals preview into the `#quotes-app` Vue root and the quote form layout.
- Preserve the existing subtotal, tax total, and grand total calculations and localization.
- Add a template structure regression test that rejects totals markup outside the Vue root and confirms the three calculated values remain rendered.

## Capabilities

### New Capabilities

- None.

### Modified Capabilities

- `quotes-ui`: Ensure calculated quote totals are rendered by Vue within the quote form rather than exposed as raw interpolation text.

## Impact

- Frontend template: `src/main/resources/templates/quotes/list.html`.
- Frontend asset tests: quote template structure and totals rendering assertions.
- No API, database, calculation, dependency, or authorization changes.
