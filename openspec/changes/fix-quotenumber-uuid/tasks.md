## 1. Fix quote number generation

- [x] 1.1 Change `QuoteService.create()` to pass `"Q-" + uuid.toString().substring(0, 8)` instead of `null` for `quoteNumber`

## 2. Verify

- [x] 2.1 Run existing tests (`QuoteServiceUnitTest`, `QuoteRepositoryIntegrationTest`, `QuoteApiControllerContractTest`) to confirm no regressions
- [x] 2.2 Verify the unique constraint is no longer violated by creating two quotes sequentially
