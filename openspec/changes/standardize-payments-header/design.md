## Context

The Payments page uses a custom `payments-header` containing a Dashboard button, while the other management pages use a dark responsive navbar with a Dashboard link and logout button. The Payments controller already resolves both Dashboard and logout message keys, and the page script already has the payment workflow state but no logout action.

## Goals / Non-Goals

**Goals:**

- Reuse the established management-page navbar structure and visual language.
- Add the existing `erpApi.logout()` flow with a `loggingOut` state.
- Preserve all existing payment forms, workflow actions, local assets, and responsive behavior.

**Non-Goals:**

- Changing payment APIs, authorization rules, payment business logic, or database schema.
- Introducing a shared component refactor across every page.

## Decisions

- Adapt the Payments template to the existing page navbar pattern rather than inventing a new header component. This minimizes risk and matches the already-used Expenses/Quotes/Invoices/Banking behavior.
- Use the existing `payments.nav.dashboard` and `payments.nav.logout` message keys, changing only the Traditional Chinese value of the Dashboard label to `工作台` for consistency.
- Implement logout in `payments.js` using the same `loggingOut`/redirect convention used by other pages; disable the control while the request is pending.
- Extend asset tests to assert both navigation controls and the absence of the old standalone header pattern.

## Risks / Trade-offs

- [Risk] Navbar markup may change available horizontal space on small screens. → Reuse Bootstrap responsive classes and add narrow-screen asset assertions.
- [Risk] Payments has existing page-specific CSS for `payments-header`. → Remove or leave unused only the relevant header rules and verify no workflow selectors depend on them.
