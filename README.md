## Summary

Operations API is a secure REST service that executes math operations for authenticated users, tracks usage as records, and manages user balances. It includes JWT auth (access/refresh), logging, stable pagination, and OpenAPI/Swagger documentation.

## Features

- **JWT authentication**: login, access and refresh tokens, JTI extraction, token blacklist.
- **Route authorization**: modern Spring Security 6 configuration.
- **Security headers**: HSTS, X-Frame-Options, X-Content-Type-Options, X-XSS-Protection, Referrer-Policy, Permissions-Policy.
- **Business operations**: execute operations, apply cost, and deduct user balance with validations.
- **Safe concurrency**: repository method with `PESSIMISTIC_WRITE` to update balances safely.
- **Flyway migrations**: versioned schema for MariaDB with UUID stored as `CHAR(36)`.
- **DTO mapping**: controllers expose DTOs; MapStruct for mappings.
- **Stable pagination**: `PageSerializationMode.VIA_DTO` for deterministic page responses.
- **Basic observability**: service-level logging.
- **OpenAPI/Swagger UI**: interactive documentation.

## Used Libraries

- Spring Boot Starters: web, webflux (client), security, data-jpa, validation, cache
- Flyway Core + MySQL
- MariaDB JDBC Driver
- JJWT (api/impl/jackson)
- Resilience4j (spring-boot3, circuitbreaker, retry)
- MapStruct
- Lombok
- Springdoc OpenAPI (webmvc-ui)

## Design Decisions

- **UUID on MariaDB**: use `CHAR(36)` for `id` columns via migrations and `@Column(columnDefinition = "CHAR(36)")` in entities for compatibility and Hibernate validation.
- **Non-negative balance**: Bean Validation (`@PositiveOrZero`) and domain rule in `deductBalance` to allow only positive amounts and check sufficient balance.
- **Balance concurrency**: `findByIdForUpdate` with `@Lock(PESSIMISTIC_WRITE)` to avoid race conditions on debits.
- **Token blacklist**: simple in-memory blacklist with Spring Cache.
- **Security headers**: configured in `SecurityFilterChain` to harden HTTP surface.
- **Deterministic pagination**: `VIA_DTO` avoids reliance on internal `PageImpl` shape during serialization.
- **Migrations first**: `ddl-auto: validate` + Flyway ensures consistent schema across environments.

## Getting Started (Setup & Run)

### Prerequisites

- Java 21
- Docker (to run MariaDB via Compose) or local MariaDB
- Git

### 1) Clone the repository

```bash
git clone https://github.com/your-org/operations-api.git
cd operations-api
```

### 2) Database (choose one)

- Using Docker Compose (recommended):

```bash
docker compose up -d
```

- Or run MariaDB locally:
  - DB: `operations_db`
  - User: `operations`
  - Password: `secret`

### 3) Environment variables (defaults exist; set strong values for production)

- `DB_URL` (e.g. `jdbc:mariadb://localhost:3306/operations_db`)
- `DB_USERNAME` (e.g. `operations`)
- `DB_PASSWORD` (e.g. `secret`)
- `JWT_SECRET` (required in prod; strong secret)
- `JWT_EXPIRATION` (ms, default 86400000)
- `JWT_REFRESH_EXPIRATION` (ms, default 604800000)

Example (Linux/macOS):

```bash
export DB_URL="jdbc:mariadb://localhost:3306/operations_db"
export DB_USERNAME="operations"
export DB_PASSWORD="secret"
export JWT_SECRET="change_me_to_strong_secret"
```

### 4) Build and run

```bash
./gradlew clean build
# Default app port is 80 in application.yml; for local dev, override to 8080:
./gradlew bootRun -Dserver.port=8080
```

### 5) Access

- Swagger UI: http://localhost:8080/operations/api/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/operations/api/api-docs

### 6) Tests

```bash
./gradlew test
```

### Notes

- Flyway runs automatically at startup.
- If DB host/credentials differ, adjust `DB_URL/DB_USERNAME/DB_PASSWORD`.
