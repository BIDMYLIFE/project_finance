# Authentication Deployment Defaults

- SQL Server schema: `SOUTHWND`; connection values come from `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD`.
- `JWT_SECRET` is required and must be supplied by the deployment secret store. The application has no fallback secret.
- Access JWT TTL is 15 minutes; refresh session TTL is 30 days.
- Cookies are HttpOnly, Secure by default, SameSite `Strict`, path `/`, and use `erp_access`/`erp_refresh` names.
- Argon2id defaults are salt 16 bytes, hash 32 bytes, parallelism 1, memory 65536 KiB, and 3 iterations.
- Bootstrap is enabled by `AUTH_BOOTSTRAP_ENABLED=true` until the database marker is initialized.
- The current deployment assumes same-origin UI/API traffic. Cross-origin deployment requires an explicit CORS and CSRF decision before enabling it.