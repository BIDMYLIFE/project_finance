# Financial ERP

A lightweight, offline-first financial ERP application for managing the complete sales and cash-flow lifecycle:

```text
Customers / Products -> Quotes -> Invoices -> Payments -> Bank Posting -> Reports
```

The application is built with Spring Boot and Spring MVC, with a responsive server-rendered UI and a REST API for business operations.

> Status: active development. Some planned capabilities and deployment decisions may still change.

## Features

- Customer and product master data
- Quote lifecycle management
- Invoice creation, issuing, cancellation, payment allocation, and balances
- Payment recording with categories, reasons, notes, receipt numbers, and reprinting
- Pending-deposit workflow for payments that have not yet been posted to a bank account
- Multiple bank accounts with credit/debit transactions and balance tracking
- Expense and expense-category management
- Reporting dashboard with filters, summaries, source references, and export support
- One-time organization and `ADMIN` bootstrap flow
- JWT authentication with refresh sessions and logout
- Argon2id password hashing
- Traditional, responsive UI with localized English and Traditional Chinese messages
- Offline frontend assets; no runtime CDN dependency

## Technology stack

- Java 21
- Spring Boot 3.5.5
- Spring MVC and Thymeleaf
- Spring Data JPA and Flyway
- Spring Security and JJWT
- Microsoft SQL Server
- Vue 3, Bootstrap 5.3, Axios, and SweetAlert2 via local WebJars
- Apache POI for spreadsheet-related exports
- OpenPDF for PDF generation
- Maven

## Requirements

Install the following before running the application:

- Java 21 or newer
- Maven 3.9 or newer
- Microsoft SQL Server

The configured database user must be able to create and update the `SOUTHWND` schema, because Flyway applies the database migrations on startup.

## Configuration

The application reads deployment-specific values from environment variables. At minimum, set:

```powershell
$env:DB_PASSWORD = "your-database-password"
$env:JWT_SECRET = "replace-with-a-long-random-secret"
```

Common configuration variables:

| Variable                   | Default              | Description                                  |
| -------------------------- | -------------------- | -------------------------------------------- |
| `DB_URL`                 | Local SQL Server URL | JDBC connection URL                          |
| `DB_USERNAME`            | `sa`               | Database username                            |
| `DB_PASSWORD`            | —                   | Database password; required                  |
| `DB_SCHEMA`              | `SOUTHWND`         | SQL Server schema and Flyway schema          |
| `JWT_SECRET`             | —                   | JWT signing secret; required                 |
| `JWT_ACCESS_TTL`         | `PT8H`             | Access token lifetime                        |
| `JWT_REFRESH_TTL`        | `P30D`             | Refresh session lifetime                     |
| `AUTH_BOOTSTRAP_ENABLED` | `true`             | Enables first-admin initialization           |
| `AUTH_COOKIE_SECURE`     | `true`             | Sends authentication cookies only over HTTPS |

Never commit passwords, JWT secrets, or other deployment credentials to source control.

## Running locally

From the project root:

```powershell
.\start.ps1
```

The script checks for Java, Maven, and `JWT_SECRET`, then starts the application with `mvn spring-boot:run`.

Alternatively:

```powershell
mvn spring-boot:run
```

The default application URL is:

```text
http://localhost:8080
```

On the first run, open `/auth/bootstrap` to create the organization and initial `ADMIN` account while bootstrap is enabled. After initialization, use `/auth/login`.

## Main pages

| Page              | Path                |
| ----------------- | ------------------- |
| Dashboard         | `/`               |
| Login             | `/auth/login`     |
| Initial bootstrap | `/auth/bootstrap` |
| Customers         | `/customers`      |
| Products          | `/products`       |
| Quotes            | `/quotes`         |
| Invoices          | `/invoices`       |
| Payments          | `/payments`       |
| Banking           | `/banking`        |
| Expenses          | `/expenses`       |
| Reporting         | `/reporting`      |

## API overview

The REST API is versioned under `/api/v1` and returns DTO-based JSON responses.

- Authentication: `/api/v1/auth`
- Customers and products: `/api/v1/customers`, `/api/v1/products`
- Quotes: `/api/v1/quotes`
- Invoices: `/api/v1/invoices`
- Payments and payment categories: `/api/v1/payments`
- Expenses: `/api/v1/expenses`
- Expense categories: `/api/v1/expense-categories`
- Bank accounts: `/api/v1/bank-accounts`
- Reports: `/api/v1/reports`

List endpoints support consistent pagination parameters. Protected operations require a valid authentication session.

## Project structure

```text
src/main/java/com/example/erp/
├── controller/mvc/   Page controllers
├── controller/api/   REST controllers
├── service/          Business rules and transactions
├── repository/       Persistence and queries
├── dto/              API request and response models
├── entity/           JPA entities
├── security/         JWT and authentication support
├── reporting/        Report queries, policies, and exports
└── config/           Application configuration
```

Frontend resources are stored under `src/main/resources/static`, Thymeleaf views are under `src/main/resources/templates`, and database migrations are under `src/main/resources/db/migration`.

## Testing and build

Run the test suite:

```powershell
mvn test
```

Build the executable JAR:

```powershell
mvn clean package
```

The project includes unit, repository integration, API contract, authentication, security, workflow asset, and offline-resource tests.

## Architecture principles

- Controllers handle HTTP concerns only: parsing, validation, service calls, and response assembly.
- Business rules and transaction boundaries live in services.
- Database access is performed through repositories.
- MVC page controllers and REST API controllers remain separate.
- Organization-scoped data is isolated by `organization_id`.
- Frontend dependencies are served locally so the main UI can operate without external network access.

## Security notes

- Passwords are stored as Argon2id hashes.
- JWT signing secrets are supplied through environment variables.
- Authentication uses HttpOnly cookies by default.
- Refresh sessions are persisted so logout can revoke them.
- Error responses intentionally avoid exposing stack traces and internal exception details.
- For production, use HTTPS, a strong randomly generated `JWT_SECRET`, a dedicated database user, and a managed secret store.

## License

No license has been selected for this repository yet. Until a license is added, all rights are reserved by the copyright holder.
