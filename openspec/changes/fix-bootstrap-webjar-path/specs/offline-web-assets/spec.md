## Purpose

This capability ensures the Dashboard can load its required Bootstrap assets from the application package in an offline deployment, using stable versioned paths that resolve to real local resources.

## ADDED Requirements

### Requirement: Dashboard assets resolve from local packaged resources

The Dashboard SHALL reference the locked Bootstrap 5.3.8 stylesheet and JavaScript bundle through local application resource paths that resolve to the packaged assets.

#### Scenario: Dashboard loads Bootstrap stylesheet

- **WHEN** a browser requests the Bootstrap stylesheet referenced by the Dashboard
- **THEN** the application SHALL resolve the request to `dist/css/bootstrap.min.css` from the local Bootstrap 5.3.8 package and return a successful stylesheet response

#### Scenario: Dashboard loads Bootstrap bundle

- **WHEN** a browser requests the Bootstrap bundle referenced by the Dashboard
- **THEN** the application SHALL resolve the request to `dist/js/bootstrap.bundle.min.js` from the local Bootstrap 5.3.8 package and return a successful JavaScript response

#### Scenario: Dashboard has no external Bootstrap dependency

- **WHEN** the Dashboard document is rendered in an environment without Internet access
- **THEN** all Bootstrap asset references SHALL remain same-origin local paths and the page SHALL not require a CDN request for Bootstrap