## Why

The Dashboard's mobile navigation toggle sits on a dark navigation bar but currently relies on Bootstrap's default toggler styling. Its icon and border can therefore be difficult to distinguish, making the primary way to reveal navigation unclear on narrow screens.

This should be corrected before the Dashboard is relied on at mobile widths so the navigation control remains discoverable and usable without changing the available routes or navigation behavior.

## What Changes

- Define an explicit high-contrast visual treatment for the Dashboard mobile menu toggle, including its icon, border, and focus state.
- Ensure the toggle remains visible against the existing dark navigation background in its default, hover, expanded, and keyboard-focus states.
- Verify the control at mobile viewport sizes and confirm that the expanded navigation does not overlap or obscure the toggle.
- Preserve the existing Bootstrap collapse behavior, accessible name, `aria-controls`, `aria-expanded`, local asset policy, and navigation routes.
- Do not alter capability availability, disabled/coming-soon semantics, logout behavior, or backend APIs.

## Capabilities

### New Capabilities

- `dashboard-mobile-menu-contrast`: Visibility, contrast, focus, and responsive behavior of the Dashboard mobile navigation toggle.

### Modified Capabilities

None.

## Impact

- `src/main/resources/templates/dashboard.html`: retain the mobile navbar toggler contract and add only the styling hook required by the design.
- `src/main/resources/static/css/dashboard.css`: define the toggler colors, icon treatment, focus visibility, and narrow-viewport layout rules.
- `src/test/java/com/example/erp/web/DashboardNavigationTest.java` and browser/accessibility checks: verify markup, local assets, contrast, and responsive behavior.
- No API, database, dependency, route, or external asset changes.
