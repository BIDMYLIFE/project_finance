## Context

See `proposal.md` for motivation. The project already has separated MVC page controllers and Web API controllers, organization-scoped master-data services, Vue page modules, local WebJars, and localized message injection. The database migration already contains `SOUTHWND.quotes` and `SOUTHWND.quote_lines`; the current repository has only the `QuoteStatus` enum and no quote aggregate implementation.

The quote flow crosses persistence, calculation, lifecycle validation, API contracts, and a responsive page. It must also remain compatible with the existing authentication/organization context and with future invoice conversion described by the financial ERP change.

## Goals / Non-Goals

**Goals:**

- Add a quote aggregate implementation that persists header and line snapshots within one organization scope.
- Expose stable list, detail, create, update, and lifecycle API contracts for the `/quotes` page.
- Reuse the existing money/tax conventions and active customer/product validation rather than duplicating master-data rules in the browser.
- Build the page from the existing customers/products interaction pattern, with a repeatable line editor and clear state-dependent actions.
- Keep submitted and terminal documents immutable while allowing draft correction.

**Non-Goals:**

- Invoice creation or quote-to-invoice conversion.
- Invoice numbering, payment allocation, printing, reporting, or bulk quote import/export.
- Hard deletion or archival storage separate from the existing quote status.
- A new frontend framework, external dependency, or client-side replacement for server-side calculation.

## Decisions

### Use a quote application service as the aggregate boundary

The API controller will accept DTOs and delegate to a quote service. The service will resolve the authenticated organization, validate the customer and products through repositories, create line snapshots, calculate totals, and persist the quote and lines atomically. Repositories will expose organization-scoped queries and will not contain lifecycle or pricing policy.

This follows the existing `Controller -> Service -> Repository` boundary and prevents a controller or Vue module from deciding whether a customer/product is usable. Direct repository access from the page controller is not used because it would duplicate the existing API/page separation.

### Keep calculation authoritative on the server

The service will calculate each line from quantity, unit price, discount, and tax rate using `BigDecimal`, fixed scale, and the project's established rounding rule. The response will include line totals and document subtotal, tax total, and grand total. The UI may show a preview using the same fields, but the saved response is authoritative and the browser will refresh the list after save or lifecycle actions.

A browser-only calculation was rejected because product prices and tax policies can change, and because it cannot safely enforce organization scope or prevent tampered totals.

### Use DTOs with explicit nested line data

Create/update requests will contain customer id, currency code, valid-until date, and a non-empty list of line requests. Each line identifies an active product and provides quantity and discount; the service snapshots product name, description, unit price, and tax rate at save time. Responses will contain quote identity, customer summary, status, dates, currency, totals, and line snapshots.

The API will expose:

- `GET /api/v1/quotes` for bounded, organization-scoped list queries with keyword, status, and page parameters.
- `GET /api/v1/quotes/{id}` for a complete detail response.
- `POST /api/v1/quotes` to create a `DRAFT`.
- `PUT /api/v1/quotes/{id}` to replace editable draft content.
- `POST /api/v1/quotes/{id}/submit`, `/accept`, `/reject`, and `/cancel` for explicit lifecycle commands.

Separate command endpoints are preferred over a generic status update because allowed transitions and authorization errors remain explicit. `cancel` changes status and never deletes rows.

### Centralize the lifecycle transition table

The service will define legal transitions as a single domain rule:

```text
DRAFT  -> SENT, CANCELLED
SENT   -> ACCEPTED, REJECTED, EXPIRED, CANCELLED
ACCEPTED / REJECTED / EXPIRED / CANCELLED -> terminal
```

An expired `SENT` quote will be treated as `EXPIRED` when read or when a lifecycle command evaluates the date. The transition check and persistence occur in one transaction. The UI will derive available buttons from the returned status and still handle a server-side rejection caused by concurrent changes.

A generic enum-order comparison was rejected because lifecycle meaning is not ordinal and would permit invalid transitions.

### Use server-side list filtering and page metadata

The list endpoint will follow the existing `PageResponse` contract and bounded `PageQuery` behavior. Keyword matching will cover quote number and customer name as supported by the repository query; status filtering will be optional, with a default that shows active workflow states. Detail loading will be separate so the list remains small while the edit modal can load complete lines.

Client-side loading of all quotes was rejected because it breaks bounded queries and makes organization isolation harder to reason about.

### Add a dedicated `/quotes` page module and reusable line-editor state

The MVC controller will render `templates/quotes/list.html` and inject only the required i18n messages. `quotes-api.js` will contain Axios calls, while `pages/quotes.js` will own list state, form state, line add/remove, validation, lifecycle commands, and retry behavior. The template will use Bootstrap grid/table/modal patterns already established by customers/products.

The line editor will keep stable row keys and disable edits during save. Customer and product selectors will load only active records through the existing APIs or a narrowly scoped quote form endpoint; selected product values are copied into the request, while the server revalidates and snapshots them.

A second frontend state store or direct DOM manipulation was rejected because it would diverge from the established page modules.

### Keep Dashboard capability registration declarative

Only the registry entry changes from `route: null, available: false` to `route: '/quotes', available: true`. Dashboard rendering already consumes the registry, so no conditional quote-specific markup is needed. A navigation test will assert both route and availability.

## Risks / Trade-offs

- [Existing quote schema has nullable quote_number and no visible quote repository contract] -> Treat quote number as an optional persisted field in this change unless the existing financial-core contract requires generation; do not invent invoice-style numbering.
- [Products or customers can become inactive between form load and save] -> Revalidate all referenced ids in the service transaction and return field/business errors without partial persistence.
- [Concurrent lifecycle commands can race] -> Load and transition within a transaction, enforce the current-state rule server-side, and let the centralized error contract report a rejected stale command.
- [Expired status may differ between list and command timing] -> Evaluate validity against the current date in the service and return the resulting status in the response; do not rely on a background scheduler for MVP.
- [Large line collections can make the modal unwieldy on mobile] -> Use bounded line counts appropriate to the existing document constraints, responsive stacked fields, horizontal table scrolling where needed, and clear add/remove controls.
- [The financial-core backend tasks may not yet be applied] -> Keep this change's tasks explicit about required entity/repository/service/API foundations and verify integration against the existing authentication and migration contracts before UI acceptance.

## Migration Plan

1. Add quote entity mappings, repositories, DTOs, calculation/lifecycle service, and API tests against the existing migration.
2. Add the `/quotes` MVC controller, localized messages, API module, page module, template, styles, and focused page/navigation tests.
3. Update the capability registry and run the existing build plus quote-focused tests; verify authenticated organization isolation and desktop/mobile states.
4. Deploy without destructive schema changes. Roll back by disabling the registry route and removing the quote page/API implementation; existing quote tables remain intact.

## Open Questions

None that change the current specification or implementation approach. Invoice conversion remains a separate financial-core task.
