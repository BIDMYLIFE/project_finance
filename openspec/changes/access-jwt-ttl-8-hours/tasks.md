## 1. Configuration

- [x] 1.1 Change the default `JWT_ACCESS_TTL` fallback from `PT15M` to `PT8H` without changing the refresh TTL.
- [x] 1.2 Verify `SecurityProperties`, JWT `exp`, access cookie Max-Age and refresh session expiry use their intended independent settings.

## 2. Tests and documentation

- [x] 2.1 Add or update authentication tests for the eight-hour access token and cookie lifetime.
- [x] 2.2 Add coverage for `JWT_ACCESS_TTL` override and the unchanged thirty-day refresh lifetime.
- [x] 2.3 Run the project test suite, package build and OpenSpec validation.
