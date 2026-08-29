## Context

The existing quote page calculates `previewTotals` in `quotes.js`, but an accidental duplicate totals block appears before the document type declaration and outside `#quotes-app`. The change must preserve the calculation and localization while restoring Vue ownership of the markup.

## Goals / Non-Goals

**Goals:**

- Keep exactly one totals preview block in the quote form.
- Place it under the Vue mount root so its bindings are evaluated.
- Add a regression assertion for the markup boundary and calculated bindings.

**Non-Goals:**

- Changing quote calculation rules, API payloads, database fields, or currency behavior.
- Introducing client-side libraries or changing the existing visual language.

## Decisions

- Remove the misplaced leading totals block and place the existing block after the quote line editor within the form. This reuses the already-tested `previewTotals` computed property and avoids duplicating calculation logic.
- Use a template asset test that checks document order, the `quotes-app` boundary, and the presence of all three bindings. This catches both the original misplaced block and accidental removal without requiring a browser dependency.
- Prefer a single `<dl>` with `dt`/`dd` pairs for accessible label/value semantics rather than converting the values to plain decorative text.

## Risks / Trade-offs

- [Risk] A string-based structure test cannot execute Vue rendering. → Keep the existing quote calculation unit tests and assert that the template places the bindings inside `#quotes-app`; browser-level testing can verify final rendering when available.
- [Risk] Moving the block may alter spacing in narrow layouts. → Reuse the existing `.totals-preview` CSS and run the existing asset/RWD tests.
