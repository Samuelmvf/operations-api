# What this project is
Operations API: a secure REST API that performs mathematical operations for authenticated users, tracks usage as records, and manages user balance. It includes JWT-based auth, pagination, logging, and OpenAPI docs.

## Tech stack
Java 21, Gradle  
Spring Boot 3.3  
Spring Web (MVC) + WebFlux client (for external calls)  
Spring Data JPA (Hibernate)  
Spring Security (JWT)  
Flyway (DB migrations)  
Spring Cache (token blacklist)  
Database: MariaDB  
Resilience4j (circuit-breaker/retry)  
MapStruct (mappers)  
Lombok (boilerplate)  
Springdoc OpenAPI (Swagger UI)  

## Architecture
Layered: controllers → services → repositories → database  
DTOs and mappers (MapStruct) isolate API from entities  
Security: JWT filter, token provider, token blacklist  
Persistence: JPA entities with soft delete, auditing  
Pagination: stable JSON via Spring Data Web Support (VIA_DTO)  
External integration: WebClient + Resilience4j for random string operation  

## Notable dependencies
```
org.springframework.boot:spring-boot-starter-{web,webflux,security,data-jpa,validation,cache}
org.flywaydb:flyway-core, flyway-mysql
io.github.resilience4j:resilience4j-{spring-boot3,circuitbreaker,retry}
io.jsonwebtoken:jjwt-{api,impl,jackson}
org.springdoc:springdoc-openapi-starter-webmvc-ui
org.mapstruct:mapstruct (+ processor)
org.projectlombok:lombok
```

## How to run locally

### Prerequisites:
Java 21  
Docker (for MariaDB) or a local MariaDB  
Git  

### 1) Clone
```bash
git clone https://github.com/your-org/operations-api.git
cd operations-api
```

### 2) Start database (choose one)
Using Docker Compose (recommended):
```bash
docker compose up -d
```
OR run MariaDB yourself and create DB:  
DB name: operations_db  
User: operations  
Password: secret  

### 3) Configure environment variables (optional; defaults exist)
DB_URL: jdbc:mariadb://localhost:3306/operations_db  
DB_USERNAME: operations  
DB_PASSWORD: secret  
JWT_SECRET: a strong random string (required for production)  
JWT_EXPIRATION: 86400000  
JWT_REFRESH_EXPIRATION: 604800000  

Example (PowerShell):
```powershell
$env:DB_URL="jdbc:mariadb://localhost:3306/operations_db"
$env:DB_USERNAME="operations"
$env:DB_PASSWORD="secret"
$env:JWT_SECRET="change_me_to_strong_secret"
```

### 4) Build and run
```bash
./gradlew clean build
./gradlew bootRun
```

### 5) Access
Swagger UI: http://localhost:8080/operations/api/swagger-ui.html  
OpenAPI JSON: http://localhost:8080/operations/api/api-docs  

### 6) Quick test
Register: POST /operations/api/v1/auth/register  
Login: POST /operations/api/v1/auth/login  
Use “Authorize” in Swagger with “Bearer <accessToken>” to call protected endpoints.  

### 7) Run tests
```bash
./gradlew test
```

## Notes:
Flyway runs automatically at startup.  
If using different DB credentials/host, set DB_URL/DB_USERNAME/DB_PASSWORD accordingly.
