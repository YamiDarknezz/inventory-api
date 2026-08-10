<div align="center">

# Inventory API

**REST inventory API with JWT authentication, role-based access control and full input validation**

[![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk&logoColor=white)](https://adoptium.net)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5.3-6DB33F?logo=spring&logoColor=white)](https://spring.io/projects/spring-boot)
[![Maven](https://img.shields.io/badge/Maven-C71A36?logo=apachemaven&logoColor=white)](https://maven.apache.org)
[![TypeScript](https://img.shields.io/badge/Playwright_Tests-18_tests-2EAD33?logo=playwright&logoColor=white)](https://github.com/YamiDarknezz/playwright-test-automation-demo)

[![CI](https://github.com/YamiDarknezz/inventory-api/actions/workflows/ci.yml/badge.svg)](https://github.com/YamiDarknezz/inventory-api/actions/workflows/ci.yml)
[![Quality Gate](https://sonarcloud.io/api/project_badges/quality_gate?project=YamiDarknezz_inventory-api)](https://sonarcloud.io/summary/new_code?id=YamiDarknezz_inventory-api)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=YamiDarknezz_inventory-api&metric=coverage)](https://sonarcloud.io/summary/new_code?id=YamiDarknezz_inventory-api)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=YamiDarknezz_inventory-api&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=YamiDarknezz_inventory-api)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=YamiDarknezz_inventory-api&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=YamiDarknezz_inventory-api)
[![Duplicated Lines](https://sonarcloud.io/api/project_badges/measure?project=YamiDarknezz_inventory-api&metric=duplicated_lines_density)](https://sonarcloud.io/summary/new_code?id=YamiDarknezz_inventory-api)

</div>

---

## 📋 About

A production-ready REST API for product inventory management, built as a portfolio showcase of backend engineering and test automation. It is the **system under test** for the [Playwright test automation demo](https://github.com/YamiDarknezz/playwright-test-automation-demo).

## ✨ Features

| | |
|---|---|
| 🔐 **JWT authentication** | `register` / `login` with BCrypt-hashed passwords |
| 🛡️ **RBAC** | `ADMIN` (write access) vs `USER` (read-only) |
| 📦 **Products CRUD** | Pagination, search by name, full validation |
| 🧾 **Consistent errors** | `400` / `401` / `403` / `404` / `409` in a uniform envelope |
| 📖 **OpenAPI/Swagger** | Interactive docs with Bearer auth scheme |
| 🗄️ **Databases** | H2 in-memory (dev) · PostgreSQL via `prod` profile (Neon-ready) |
| ✅ **Quality gates** | JaCoCo ≥90% lines / ≥70% branches enforced in CI · SonarCloud scan |
| 🧪 **33 tests** | Auth, CRUD, RBAC, validation, error handling |

## 📊 Quality

| Metric | Value |
|---|---|
| Quality Gate (SonarCloud) | **PASS** ✅ |
| Coverage | **96.3%** |
| Vulnerabilities / Bugs / Hotspots | **0 / 0 / 0** |
| Security & Reliability rating | **A / A** |
| Duplicated lines | **0%** |
| Code smells | 1 (INFO: jjwt API requires `java.util.Date`, `java.time` used internally) |

## 🚀 Quick start (dev)

```bash
./mvnw spring-boot:run
```

Swagger UI: http://localhost:8080/swagger-ui.html

**Live demo**: https://api-inventory.darknezz.dev/swagger-ui.html

Seeded credentials (dev profile):

| Username | Password | Role |
|----------|----------|------|
| `admin` | `admin123` | ADMIN |
| `demo` | `demo1234` | USER |

## 🔑 Example flow

```bash
# 1. Login as admin
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# 2. Create a product with the returned token
curl -X POST http://localhost:8080/api/products \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"name":"USB-C Cable","description":"2m braided cable","price":9.99}'

# 3. List products (any authenticated role)
curl http://localhost:8080/api/products \
  -H "Authorization: Bearer <TOKEN>"
```

## 🗺️ Endpoints

| Method | Path | Access | Description |
|--------|------|--------|-------------|
| POST | `/api/auth/register` | Public | Register a user, returns JWT |
| POST | `/api/auth/login` | Public | Login, returns JWT |
| GET | `/api/products` | USER, ADMIN | List all products |
| GET | `/api/products/paged?page=0&size=5&search=usb` | USER, ADMIN | Paginated products |
| GET | `/api/products/{id}` | USER, ADMIN | Get product by id |
| POST | `/api/products` | ADMIN | Create product |
| PUT | `/api/products/{id}` | ADMIN | Update product |
| DELETE | `/api/products/{id}` | ADMIN | Delete product |
| GET | `/api/users` | ADMIN | List users |
| GET | `/api/users/{id}` | ADMIN | Get user |
| GET | `/actuator/health` | Public | Health check |

## 🏗️ Architecture

```
src/main/java/com/darkhub/api/inventory/
├── config/       # Security (JWT filter, BCrypt), OpenAPI, dev data seeding
├── controller/   # Auth, Product, User controllers
├── dto/          # Request/response records with Bean Validation
├── exception/    # GlobalExceptionHandler: 400/401/403/404/409
├── model/        # User, Product, Role
├── repository/   # Spring Data JPA
├── security/     # JwtService, JwtAuthFilter
└── service/      # Business logic (auth, products, users)
```

## 🔧 CI/CD

**Pipeline (`ci.yml`)**: `Build` → `Test` → `Deploy` (main only)

| Job | What it does |
|---|---|
| Build | `package`, uploads jar as artifact |
| Test | `verify` + JaCoCo coverage gate (≥90% lines, ≥70% branches), coverage report artifact |
| Deploy | SSH to Oracle Cloud VM: git pull + docker compose up -d --build (auto-deploy on every push to main) |

**Quality (`code-quality.yml`)**: SonarCloud scan on every push/PR.

## 🗄️ Production (Neon PostgreSQL)

Set profile `prod` and env vars (see `.env.example`):

```bash
SPRING_PROFILES_ACTIVE=prod
DB_URL=postgresql://user:pass@ep-xxx.pooler.us-east-1.aws.neon.tech/inventory?sslmode=require
DB_USER=...
DB_PASSWORD=...
JWT_SECRET=<long random secret, at least 32 bytes>
```

```bash
docker build -t inventory-api .
docker run -p 8080:8080 --env-file .env inventory-api
```

## 🧪 Running tests

```bash
./mvnw verify
```

Coverage enforced by JaCoCo: **≥90% lines, ≥70% branches**. Current: **98% lines / 78% branches**.

## 📚 Related

- [Playwright test automation demo](https://github.com/YamiDarknezz/playwright-test-automation-demo) — 26 API + UI E2E tests covering this API, CI green, live HTML report
- [GitHub profile](https://github.com/YamiDarknezz)

---

<div align="center">

**Built with Java 21 · Spring Boot 3.5 · PostgreSQL · Docker · GitHub Actions · SonarCloud**

</div>
