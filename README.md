# Inventory API

[![CI](https://github.com/YamiDarknezz/inventory-api/actions/workflows/ci.yml/badge.svg)](https://github.com/YamiDarknezz/inventory-api/actions/workflows/ci.yml)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=YamiDarknezz_inventory-api&metric=coverage)](https://sonarcloud.io/summary/new_code?id=YamiDarknezz_inventory-api)
[![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=YamiDarknezz_inventory-api&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=YamiDarknezz_inventory-api)

REST API for product inventory management with **JWT authentication**, **role-based access control** and **full input validation**. Built with Java 21 + Spring Boot 3.5.

This API is the system under test for the [Playwright test automation demo](https://github.com/YamiDarknezz/playwright-test-automation-demo).

## Features

- JWT authentication (`/api/auth/register`, `/api/auth/login`)
- Roles: `ADMIN` (write access) and `USER` (read-only)
- Products: CRUD, pagination and search by name
- Consistent error responses: `400` validation, `401` unauthenticated, `403` forbidden, `404` not found, `409` conflict
- OpenAPI/Swagger UI with Bearer auth scheme
- H2 in-memory database for dev, PostgreSQL ready via `prod` profile
- 33 automated tests covering auth, CRUD and RBAC with **98% line / 78% branch coverage** (JaCoCo quality gate: ≥90% lines enforced in CI)

## Quick start (dev)

```bash
./mvnw spring-boot:run
```

Swagger UI: http://localhost:8080/swagger-ui.html

Seeded credentials (dev profile):

| Username | Password | Role   |
|----------|----------|--------|
| `admin`  | `admin123` | ADMIN |
| `demo`   | `demo1234` | USER  |

## Example flow

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

## Endpoints

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

## Production

Set profile `prod` and the env vars below (see `.env.example`):

```bash
SPRING_PROFILES_ACTIVE=prod
DB_HOST=...
DB_PORT=5432
DB_NAME=inventory
DB_USER=...
DB_PASSWORD=...
JWT_SECRET=<long random secret, at least 32 bytes>
```

```bash
docker build -t inventory-api .
docker run -p 8080:8080 --env-file .env inventory-api
```

## Tests

```bash
./mvnw verify
```

Coverage: auth (register, duplicate username/email, validation, login, bad credentials, malformed JSON), products (CRUD, RBAC, 400/401/403/404, pagination with/without search), users (RBAC), error envelope (all handlers incl. 500), entity timestamps. JaCoCo enforces **≥90% line and ≥70% branch coverage** as part of the build.
