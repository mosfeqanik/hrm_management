# English practice

I am learning English. In every reply, including replies to slash commands such as `/commit-push`, if my message has grammar, spelling or wording mistakes, start with a short "English tip": show the corrected sentence and briefly explain the main mistakes in simple English. Keep it short and kind, then do the task. Skip the tip if my message is already correct.

# Project

HRM / workforce management multi-tenant SaaS. Spring Boot API in `backend/`; `frontend/` and `mobile_app/` are not built yet.
Deeper backend notes (entities, access model, conventions): [backend/CLAUDE.md](backend/CLAUDE.md). ER diagram: [backend/docs/er-diagram.md](backend/docs/er-diagram.md).

## Run commands

All commands run from `backend/`.

```bash
# Docker Compose (builds the image, starts the API on :8080)
docker compose up --build -d      # compose auto-reads backend/.env
docker compose logs -f app
docker compose down

# Local (no Docker)
./mvnw spring-boot:run
./mvnw compile
./mvnw test
./mvnw clean package -DskipTests  # jar in target/
```

- `docker-compose.yml` has one service, `app` (container `hrm-management-api`). There is **no local Postgres container**:
  the database is Neon, given by `DB_URL`.
- Needed in `backend/.env` (gitignored; `application.properties` also imports it for local runs): `DB_URL`, `JWT_SECRET`.
  Optional: `JWT_EXPIRATION_MS` (default 3600000), `SEED_ENABLED` (default true), `SEED_ADMIN_EMAIL`, `SEED_ADMIN_PASSWORD`.
- First start seeds one `SUPER_ADMIN` (`DataSeeder`) when no user exists. Change its password after first login.
- Dockerfile is multi-stage: Maven 3.9 + Temurin 21 build, then `eclipse-temurin:21-jre` runtime.
- Swagger UI: `http://localhost:8080/swagger-ui.html` (springdoc; spec at `/v3/api-docs`). Auth is manual: log in, click Authorize, paste the JWT.
- Postman: `backend/postman/hrm_management.postman_collection.json` is generated from the controllers and DTOs.
  After adding or changing endpoints, run `python postman/generate_postman.py` from `backend/`. Do not add token-saving scripts; the user pastes the token into the `token` variable.

## Notes

- Never commit or print `backend/.env` (it holds the real Neon credentials and JWT secret).
- `backend/CLAUDE.md` still says only the entity layer exists; controllers, services, DTOs and repositories are now built.
