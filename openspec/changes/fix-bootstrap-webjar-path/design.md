## Context

The existing Dashboard uses the local `org.webjars.npm:bootstrap:5.3.8` dependency, but its stylesheet and bundle references omit the package's `dist/` directory. The JAR contains the required files under `dist/css/` and `dist/js/`; the authentication bootstrap page already demonstrates the correct stylesheet path. See `proposal.md` for the motivation and `specs/offline-web-assets/spec.md` for the behavior contract.

## Goals / Non-Goals

**Goals:**

- Make both Dashboard Bootstrap references resolve to files shipped in the existing local WebJar.
- Preserve the pinned Bootstrap version and same-origin offline behavior.
- Verify the corrected paths without requiring a running external service or Internet access.

**Non-Goals:**

- Do not change Bootstrap versions, dependency management, authentication, API routes, or page layout.
- Do not replace WebJars with copied vendor files or a CDN.

## Decisions

- Keep `org.webjars.npm:bootstrap:5.3.8` as the single asset source because the dependency is already present, version-locked, and exposes the files through Spring's WebJar resource mapping.
- Use the exact packaged paths `/webjars/bootstrap/5.3.8/dist/css/bootstrap.min.css` and `/webjars/bootstrap/5.3.8/dist/js/bootstrap.bundle.min.js`. This follows the JAR contents and keeps CSS and JavaScript references consistent.
- Validate the template references against the local JAR/resource layout and, when the application is running, verify successful same-origin responses. A CDN substitution is rejected because the project requires offline runtime assets.

## Risks / Trade-offs

- [Risk] A future Bootstrap package layout change could invalidate hard-coded paths -> [Mitigation] Keep the dependency version pinned and include a resource-path check in verification.
- [Risk] The local server may be unavailable during validation -> [Mitigation] Validate the packaged JAR and template paths independently, then perform HTTP checks when the server is available.

## Migration Plan

1. Update the Dashboard's Bootstrap CSS and bundle references to the exact paths present in the 5.3.8 WebJar.
2. Run the focused resource-path/template verification and the existing project test/build checks.
3. Open the Dashboard in a running application and confirm both resources return successfully; rollback consists of reverting the two template reference changes if needed.

## Open Questions

None.