## Why

The Dashboard requests Bootstrap assets from paths that do not exist in the `org.webjars.npm:bootstrap:5.3.8` package. The package stores the stylesheet and bundle below `dist/`, so the current CSS request fails and the page loses Bootstrap styling. The matching JavaScript path must also be kept correct so responsive navigation remains functional. This should be corrected before the Dashboard is used as the authenticated entry page.

## What Changes

- Correct the Dashboard Bootstrap stylesheet WebJar path to the packaged `dist/css` location.
- Correct the Dashboard Bootstrap bundle WebJar path to the packaged `dist/js` location.
- Add a focused verification that rendered Dashboard asset references resolve to files supplied by the locked local WebJar dependency and do not depend on an external CDN.

## Capabilities

### New Capabilities

- `offline-web-assets`: Serve and reference versioned Bootstrap assets from the local WebJar package using paths that exist in the package.

### Modified Capabilities

- None.

## Impact

- Affected view: `src/main/resources/templates/dashboard.html`.
- Affected dependency contract: local Bootstrap WebJar version `5.3.8`.
- Affected verification: template/resource-path checks and Dashboard browser loading of CSS and JavaScript.
- No API, database, authentication, or public business-domain behavior changes.