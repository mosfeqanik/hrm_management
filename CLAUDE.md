# English practice

I am learning English. In every reply, including replies to slash commands such as `/commit-push`, if my message has grammar, spelling or wording mistakes, start with a short "English tip": show the corrected sentence and briefly explain the main mistakes in simple English. Keep it short and kind, then do the task. Skip the tip if my message is already correct.

# Project

mindHR: HRM / workforce management multi-tenant SaaS. Spring Boot API in `backend/`; React web app in `frontend/`; `mobile_app/` is not built yet.
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

- `docker-compose.yml` has one service, `app` (container `mindhr-api`). There is **no local Postgres container**:
  the database is Neon, given by `DB_URL`.
- Needed in `backend/.env` (gitignored; `application.properties` also imports it for local runs): `DB_URL`, `JWT_SECRET`.
  Optional: `JWT_EXPIRATION_MS` (default 3600000), `SEED_ENABLED` (default true), `SEED_ADMIN_EMAIL`, `SEED_ADMIN_PASSWORD`.
- First start seeds one `SUPER_ADMIN` (`DataSeeder`) when no user exists. Change its password after first login.
- Dockerfile is multi-stage: Maven 3.9 + Temurin 21 build, then `eclipse-temurin:21-jre` runtime.
- Swagger UI: `http://localhost:8080/swagger-ui.html` (springdoc; spec at `/v3/api-docs`). Auth is manual: log in, click Authorize, paste the JWT.
- Postman: `backend/postman/mindhr.postman_collection.json` is generated from the controllers and DTOs.
  After adding or changing endpoints, run `python postman/generate_postman.py` from `backend/`. Do not add token-saving scripts; the user pastes the token into the `token` variable.

## Notes

- Never commit or print `backend/.env` (it holds the real Neon credentials and JWT secret).
- `backend/CLAUDE.md` still says only the entity layer exists; controllers, services, DTOs and repositories are now built.

## Frontend (`frontend/`)

Vite + React + TypeScript, FreeDash admin template (Bootstrap 5 CSS in `public/template`, `react-feather` icons; no Tailwind), React Router, TanStack Query, React Hook Form + Zod, axios. Node 22+.

```bash
# from frontend/
npm install
npm run dev        # http://localhost:5173, proxies /api -> http://localhost:8080 (the backend has no CORS config)
npm run build      # tsc -b && vite build

# Docker: start the backend compose first (it creates the backend_mindhr-net network), then:
docker compose up --build -d   # http://localhost:3000, nginx serves the SPA and proxies /api to http://app:8080
```

- Token is kept in localStorage; `src/auth/AuthContext.tsx` decodes the JWT claims (`authorities`, `dataScope`, ...). `<Can permission="EMPLOYEE_CREATE">` and the sidebar only hide UI; the backend enforces access.
- `SUPER_ADMIN` gets every HR permission at login (`CustomUserDetails.superAdminHrAuthorities`); `SAAS_STAFF` holds none (spec section 29) and gets 403 on HR endpoints.
  Platform users pick an organization in the top bar (`OrgPicker` in `AppShell`); `src/api/orgScope.ts` adds that `organizationId` to every org-scoped request (GET params, POST/PUT bodies), and org-scoped pages show "Select an organization" until one is picked. Self-service actions (apply leave, check-in) still need an employee profile.
- If the backend fails with `ClassFormatError: Invalid method Code length 0`, the VS Code Java extension wrote a bad class into `target/classes`; start with `./mvnw clean spring-boot:run`.
- Built so far: login, app shell, and screens for Organizations, Users, Subscription Plans, System Settings, Subscriptions, Billing (invoices, payments, receipts), Contracts and Support Tickets (platform side), and for Employees, Branches, Departments, Attendance (list + manual records; mobile check-in/out is not in the web app), Leave (requests with apply/approve/reject/cancel, balances, types), Shifts (roster + shifts), Payroll (runs: generate/finalize/pay/delete, payslips with adjustments) and Assets (assets, assignments with return, employee documents). Pattern: `crud()` in `src/api/crud.ts`, `useResource` for lists, `DataTable`/`Tabs`/`Modal` in `src/components/ui.tsx`. API types in `src/api/types.ts` are hand-written from the DTOs.
