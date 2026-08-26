## Context

`QuoteService.create()` saves `null` for `quote_number`. The DB has `UNIQUE (organization_id, quote_number)` and SQL Server treats multiple NULLs as duplicates, causing constraint violations on the second draft.

## Goals / Non-Goals

**Goals:**
- Eliminate the UNIQUE KEY constraint violation on quote creation
- Generate a deterministic, collision-resistant quote number with zero coordination

**Non-Goals:**
- Sequential or human-memorizable numbering (e.g. Q-0001)
- Wiring up the `document_sequences` table
- Changing the number format for invoices

## Decisions

### Use UUID substring as quote number

Generate `"Q-" + uuid.toString().substring(0, 8)` in `QuoteService.create()`.

**Why this over alternatives:**

| Alternative | Why rejected |
|---|---|
| `document_sequences` table | Requires new service, repository, migration — overkill for this fix |
| Sequential counter (atomic query) | Contention under concurrency, extra DB round-trip |
| Timestamp-based (e.g. `Q-20260826-001`) | Same NULL contention issue if two drafts created same second |
| Full UUID (36 chars) | Too long, poor readability; 8 hex chars (4 bytes = 32 bits) gives ~4 billion values per UUID space — collision probability negligible |

**Trade-off**: The number is not human-readable as a sequence. This is acceptable because the primary goal is correctness, not display. Sequential numbering can be added later via `document_sequences` without breaking this approach.

## Risks / Trade-offs

- **[Low readability]** Numbers like `Q-4e6da3b0` are less intuitive than `Q-0001`. → Mitigated by the fact that quotes are looked up by customer/status/date, not by number memorization.
- **[Same UUID, different quote]** Impossible — each quote gets a fresh `UUID.randomUUID()`.
- **[Legacy NULLs]** Existing NULL numbers are unaffected. The unique constraint allows them because there's only one NULL per org (or zero). No migration needed.
