# Backend Usage Guide

This document explains how to set up and run the Vidalia backend in three ways:

1. Local setup
2. Development mode with H2
3. Production mode with PostgreSQL
4. Docker / Docker Compose

---

## 0. How to set up everything

### Prerequisites

- Java 17
- Maven wrapper included in the repo (`./mvnw`)
- Docker and Docker Compose (for container-based setup)
- PostgreSQL if you want to run the production profile locally without Docker

### Repo files you should know about

- `src/main/resources/application-dev.properties` — development config using H2
- `src/main/resources/application-prod.properties` — production config using PostgreSQL
- `src/main/resources/application-test.properties` — test config
- `src/main/resources/db/migration/V1__init.sql` — initial schema migration
- `.env.example` — template for environment variables
- `.env` — local development environment variables for Docker Compose

### Sensitive information: where to put it

Use `.env` for sensitive values and keep it out of git.

Add or change these values in `.env`:

- `APP_SECURITY_JWT_SECRET` — required JWT signing secret
- `SPRING_DATASOURCE_PASSWORD` — database password
- `POSTGRES_PASSWORD` — Postgres container password used by Docker Compose

If you deploy somewhere else later, set the same values in that platform's secrets manager or environment settings.

Never put real secrets into `.env.example`. That file should only contain placeholders or safe defaults.

### First-time setup

If you are running locally:

```bash
# From the backend project root
./mvnw clean test
```

If you are using Docker:

```bash
mkdir -p uploads
cp .env.example .env
```

> Important: do not commit `.env`. It is ignored by git.

---

## 1. How to use the dev version of the backend with H2

The dev profile uses an in-memory H2 database. This is best for quick local work when you do not want to start PostgreSQL.

### How it works

- Profile: `dev`
- Database: H2 in-memory
- Schema: auto-updated from JPA entities
- H2 console: enabled

### Run it locally

```bash
# From the backend project root
export SPRING_PROFILES_ACTIVE=dev
./mvnw spring-boot:run
```

If you do not set `SPRING_PROFILES_ACTIVE`, the application is configured to default to `dev`.

### H2 console

When the app is running, open the H2 console in your browser:

```text
http://localhost:8080/h2-console
```

### H2 connection settings

Use these values in the H2 console:

- JDBC URL: `jdbc:h2:mem:testdb`
- User Name: `sa`
- Password: empty

### Good for

- quick API testing
- frontend work without PostgreSQL
- local development when you want minimal setup

### Not good for

- production
- checking production-specific PostgreSQL behaviour

---

## 2. How to use the prod version of the backend

The prod profile is configured for PostgreSQL and is the version the frontend team should use when they want behaviour that matches deployment more closely.

### Required environment variables

At minimum, set these variables:

- `SPRING_PROFILES_ACTIVE=prod`
- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `APP_SECURITY_JWT_SECRET`

Optional upload-related variables are already documented in `.env.example`.

### Example local prod-style setup without Docker

```bash
export SPRING_PROFILES_ACTIVE=prod
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/vidalia_db
export SPRING_DATASOURCE_USERNAME=vidalia
export SPRING_DATASOURCE_PASSWORD=changeme
export APP_SECURITY_JWT_SECRET=YOUR_STRONG_SECRET_HERE

./mvnw spring-boot:run
```

### What this profile does

- connects to PostgreSQL
- enables Flyway migrations
- uses the production upload configuration
- disables the H2 console

### PostgreSQL schema

The initial schema is defined in:

```text
src/main/resources/db/migration/V1__init.sql
```

If you change the entities, update the migration files accordingly.

### Production notes

- Use a strong JWT secret in real deployments.
- Do not commit real secrets to git.
- The frontend can use this profile for realistic API testing, but the live production deployment should be configured by the deploy target, not by hardcoding values in the codebase.

---

## 3. How to use Docker

Docker is the easiest way to run the backend together with PostgreSQL.

### Files involved

- `Dockerfile` — builds the backend image
- `docker-compose.yml` — runs app + database
- `.env` — local environment values

### Before starting

```bash
mkdir -p uploads
cp .env.example .env
```

Edit `.env` if needed. For local Docker use, the defaults are usually enough.

### Start everything

```bash
docker-compose up --build
```

### Start in the background

```bash
docker-compose up --build -d
```

### View logs

```bash
docker-compose logs -f app
docker-compose logs -f db
```

### Stop the stack

```bash
docker-compose down
```

### Reset everything including database volume

```bash
docker-compose down -v
```

### Useful Docker endpoints

Once running:

- Backend: `http://localhost:8080`
- H2 console: not used in Docker prod mode
- PostgreSQL: available inside the `db` container

### Open the database console

```bash
docker-compose exec db psql -U vidalia -d vidalia_db
```

If your `.env` uses different values, replace `vidalia` and `vidalia_db` with your own.

### Verify tables

Inside `psql`:

```sql
\dt
SELECT * FROM users LIMIT 5;
\q
```

### Notes about uploads

The backend stores uploads in the mounted `./uploads` directory on your machine. Make sure it exists:

```bash
mkdir -p uploads
```

---

## Quick API flow for frontend testing

### Register an organisation

```bash
curl -X POST http://localhost:8080/api/auth/register/organisation \
  -H "Content-Type: application/json" \
  -d '{
    "email":"org1@example.test",
    "password":"Password123!",
    "displayName":"Test Org",
    "accountType":"CHARITY"
  }'
```

### Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email":"org1@example.test",
    "password":"Password123!"
  }'
```

Use the returned `accessToken` in the `Authorization: Bearer <token>` header for protected routes.

---

## Recommended workflow for the team

1. Use H2 dev mode for quick local work.
2. Use Docker Compose if you want to match the backend more closely.
3. Use the prod profile with PostgreSQL when testing real auth and database behaviour.
4. Keep secrets only in `.env` or deployment secrets, never in git.


