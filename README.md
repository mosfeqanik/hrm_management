# hrm_management

| Folder | Purpose |
|---|---|
| `backend/` | Spring Boot API (Maven: `saas-core` + `app`), Postgres, Docker, dev/prod env files |
| `frontend/` | React/Vite client (not scaffolded yet) |
| `app/` | Empty, unused |

## Run (dev)
```bash
cd backend
cp .env.dev.example .env.dev          # then edit
docker compose --env-file .env.dev up -d postgres
set -a; . ./.env.dev; set +a          # export env for Maven
./mvnw -pl app spring-boot:run
```
API: http://localhost:8080 — frontend dev origin allowed by CORS: http://localhost:5173.

## Run (prod, docker)
```bash
cd backend
cp .env.prod.example .env.prod        # set real secrets
docker compose --env-file .env.prod --profile prod up -d --build
```

## Environment variables (backend)
| Var | Purpose |
|---|---|
| `SPRING_PROFILES_ACTIVE` | `dev` or `prod` |
| `SERVER_PORT` | API port (default 8080) |
| `DB_URL` / `DB_USER` / `DB_PASSWORD` | Postgres connection |
| `ADMIN_EMAIL` / `ADMIN_PASSWORD` / `ADMIN_NAME` | First Super Admin (only while no platform user exists) |
| `CORS_ALLOWED_ORIGINS` | Comma-separated browser origins allowed to call the API |

Frontend: set `VITE_API_BASE_URL` (see `frontend/.env.example`).
