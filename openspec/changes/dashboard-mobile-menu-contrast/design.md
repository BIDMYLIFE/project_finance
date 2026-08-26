## Context

The Dashboard uses Bootstrap's responsive navbar and collapse behavior. The navigation bar has a dark custom background, while the current toggler does not have a dedicated light-theme class or project-specific icon and border rules. See proposal.md for the motivation and spec.md for the externally observable contract.

## Goals / Non-Goals

**Goals:**

- Make the existing mobile toggler consistently visible on the dark Dashboard navigation.
- Keep the change local to the Dashboard template/style boundary and preserve Bootstrap collapse behavior.
- Make default, hover, expanded, and keyboard-focus states explicit and testable.
- Verify the actual rendered result at desktop and mobile viewports using local assets.

**Non-Goals:**

- Do not change navigation routes, capability registry, authentication, logout behavior, or APIs.
- Do not replace Bootstrap or add a new icon library, external asset, or frontend dependency.
- Do not change the visual treatment of disabled/coming-soon navigation items except where needed to prevent overlap with the toggler.

## Decisions

### Use a dedicated Dashboard toggler style after Bootstrap

Add a project-scoped selector for the Dashboard navbar toggler and its icon so the final cascade explicitly sets a light foreground, a visible boundary, and a clear focus ring over the existing dark background. The icon should use an offline-safe local/CSS mechanism and should not depend on a remote asset.

Using only Bootstrap defaults is rejected because the page does not currently opt into Bootstrap's dark navbar variables, so the default icon and border may be too dark for this navigation background. Replacing the entire navbar theme is also rejected because it would broaden the change and could alter unrelated link and disabled-state styling.

### Preserve the existing semantic control and collapse wiring

Keep the button element, accessible label, controlled-navigation identifier, and expanded-state attribute already present in the template. If a class hook is needed, add only that hook; do not add click handlers or duplicate collapse state in Vue/JavaScript.

Changing the toggler into a custom JavaScript control is rejected because it would duplicate Bootstrap collapse behavior and increase keyboard and state-synchronization risk.

### Validate computed rendering rather than source strings alone

Keep focused resource assertions for the required markup and CSS selectors, and add browser-level checks where available for computed icon/border/focus visibility and mobile expanded layout. Test both a narrow mobile viewport and a desktop viewport to catch breakpoint regressions.

Source-only assertions are insufficient because Bootstrap's cascade and data-URI icon styling determine the actual rendered color. A browser-only check is also insufficient because it can miss removed markup contracts or accidental external resource references.

## Risks / Trade-offs

- [Risk] A future Bootstrap upgrade changes navbar variable or icon behavior -> Mitigation: retain explicit Dashboard selectors and verify computed styles in the focused navigation checks.
- [Risk] Increasing contrast makes the toggle visually stronger than nearby navigation items -> Mitigation: limit the treatment to the compact control and use the existing bronze focus accent consistently.
- [Risk] Expanded navigation content wraps differently across mobile widths -> Mitigation: test at a representative narrow viewport and verify bounding boxes, overflow, and control separation.
- [Risk] A CSS/data-URI icon implementation is malformed in one browser -> Mitigation: validate the rendered icon in browser checks and keep a visible border/focus treatment as a fallback affordance.

## Migration Plan

1. Add the project-scoped toggler markup hook and explicit high-contrast styles while preserving Bootstrap collapse attributes.
2. Add focused resource and browser/accessibility checks for states, contrast, focus, offline assets, and mobile layout.
3. Run the existing test/build checks and inspect the Dashboard at mobile and desktop viewports.
4. Roll back only the template/CSS/test changes if the rendered control regresses; no data, API, or deployment migration is required.
