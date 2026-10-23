# mindHR

Multi-tenant SaaS for HR / workforce management. One SaaS company (Super Admin + SaaS staff) sells to many client
organizations; each organization is an isolated tenant with branches, employees, attendance, leave and payroll.

| Folder | Purpose |
|---|---|
| `backend/` | Spring Boot 4 API (Java 21, JPA/Hibernate, Postgres on Neon, JWT) |
| `frontend/` | React/Vite client (not scaffolded yet) |
| `mobile_app/` | Employee mobile app (placeholder) |

## Run

All commands from `backend/`. Create `backend/.env` first:

```properties
DB_URL=jdbc:postgresql://<host>/<db>?user=<user>&password=<password>&sslmode=require
JWT_SECRET=<base64 secret>
# optional
JWT_EXPIRATION_MS=3600000
SEED_ENABLED=true
SEED_ADMIN_EMAIL=admin@mindhr.local
SEED_ADMIN_PASSWORD=ChangeMe123!
```

**Docker Compose**
```bash
docker compose up --build -d
docker compose logs -f app
docker compose down
```

**Without Docker**
```bash
./mvnw spring-boot:run
./mvnw test
```

API: http://localhost:8080. On first start a `SUPER_ADMIN` is seeded from the `SEED_ADMIN_*` values; change the password
after you log in.

## API docs

- **Swagger UI:** http://localhost:8080/swagger-ui.html (OpenAPI JSON at `/v3/api-docs`). Call `POST /api/auth/login`,
  click **Authorize**, and paste the token yourself.
- **Postman:** import `backend/postman/mindhr.postman_collection.json` (214 requests, one folder per controller).
  Run `POST /api/auth/login`, then paste the token into the collection variable `token`. Nothing saves it automatically.
  Regenerate after API changes with `python postman/generate_postman.py` (from `backend/`).

## ER diagram

Platform side (SaaS company) and organization side (tenant data). Attributes are in
[backend/docs/er-diagram.md](backend/docs/er-diagram.md); this diagram shows relationships only.

```mermaid
erDiagram
    ORGANIZATIONS |o--o{ USERS : has
    USERS ||--o{ USER_ROLES : ""
    ROLES ||--o{ USER_ROLES : ""
    ROLES ||--o{ ROLE_PERMISSIONS : ""
    PERMISSIONS ||--o{ ROLE_PERMISSIONS : ""
    ORGANIZATIONS |o--o{ ROLES : "custom roles"

    LEADS |o--o| ORGANIZATIONS : converts_to
    LEADS ||--o{ SALES_ACTIVITIES : ""
    SUBSCRIPTION_PLANS ||--o{ SUBSCRIPTIONS : ""
    ORGANIZATIONS ||--o{ SUBSCRIPTIONS : ""
    SUBSCRIPTIONS ||--o{ SUBSCRIPTION_HISTORY : ""
    SUBSCRIPTIONS |o--o{ INVOICES : ""
    ORGANIZATIONS ||--o{ INVOICES : ""
    INVOICES ||--o{ PAYMENTS : ""
    PAYMENT_METHODS |o--o{ PAYMENTS : ""
    PAYMENTS ||--o| RECEIPTS : ""
    PAYMENTS ||--o{ REFUNDS : ""
    ORGANIZATIONS ||--o{ SUPPORT_TICKETS : ""
    SUPPORT_TICKETS ||--o{ TICKET_COMMENTS : ""

    ORGANIZATIONS ||--o{ BRANCHES : ""
    ORGANIZATIONS ||--o{ EMPLOYEES : ""
    BRANCHES ||--o{ DEPARTMENTS : ""
    BRANCHES |o--o{ EMPLOYEES : ""
    DEPARTMENTS |o--o{ EMPLOYEES : ""
    EMPLOYEES |o--o{ EMPLOYEES : manager
    USERS |o--o| EMPLOYEES : login

    EMPLOYEES ||--o{ EMPLOYEE_PAYMENT_ACCOUNTS : ""
    EMPLOYEES ||--o{ SALARY_STRUCTURES : ""
    EMPLOYEES ||--o{ EMPLOYEE_EVENTS : timeline
    EMPLOYEES ||--o{ EMPLOYEE_DOCUMENTS : ""

    SHIFTS ||--o{ SHIFT_ASSIGNMENTS : ""
    EMPLOYEES ||--o{ SHIFT_ASSIGNMENTS : ""
    EMPLOYEES ||--o{ ATTENDANCES : ""

    LEAVE_TYPES ||--o{ LEAVE_BALANCES : ""
    LEAVE_TYPES ||--o{ LEAVE_REQUESTS : ""
    EMPLOYEES ||--o{ LEAVE_BALANCES : ""
    EMPLOYEES ||--o{ LEAVE_REQUESTS : ""
    EMPLOYEES ||--o{ WORK_REQUESTS : ""

    BRANCHES |o--o{ PAYROLL_RUNS : ""
    PAYROLL_RUNS ||--o{ PAYSLIPS : ""
    EMPLOYEES ||--o{ PAYSLIPS : ""

    ASSETS ||--o{ ASSET_ASSIGNMENTS : ""
    EMPLOYEES ||--o{ ASSET_ASSIGNMENTS : ""
```

Tenant rule: every organization-owned table has a non-null `organization_id` (`Tenant` base class). `USERS`, `ROLES`
and `ACTIVITY_LOGS` have a nullable one (null = platform level).

## Flows

**Onboarding (SaaS side)**
```mermaid
flowchart LR
    L[Lead] --> D[Demo] --> O[Organization created] --> U[Owner user + built-in roles]
    U --> S[Subscription - trial] --> P[Payment] --> A[Organization ACTIVE]
```

**Billing**
```mermaid
flowchart LR
    S[Subscription] --> I[Invoice] --> P[Payment via gateway] --> R[Receipt]
    P -.-> F[Refund]
```

**Support**
```mermaid
flowchart LR
    T[Org user opens ticket] --> A[Assigned to SaaS support staff] --> C[Comments - internal notes staff-only] --> X[Resolved / Closed]
```

**Employee, attendance and payroll**
```mermaid
flowchart LR
    E[Employee created] --> LG[Login user with EMPLOYEE role]
    E --> SH[Shift assignment]
    SH --> AT[Mobile check-in / out: geofence + lateness]
    LV[Leave request] --> AP[Manager approves / rejects]
    AT --> PR[Payroll run]
    AP --> PR
    PR --> PS[Payslips]
```

Payroll rules: Friday off, 3 lates = 1 day deducted, no holiday calendar yet.

## Access model

- **What a user may do:** `Role` -> `Permission(module, action)`, enforced with `@PreAuthorize("hasAuthority('MODULE_ACTION')")`.
- **Which rows a user sees:** `Role.dataScope` = `PLATFORM`, `ORGANIZATION`, `BRANCH`, `TEAM` or `SELF` (widest scope wins).
  Scope, employee id and branch id are JWT claims, so changes apply at next login.
- **Built-in roles per organization:** `ORG_OWNER`, `HR_ADMIN`, `BRANCH_MANAGER`, `TEAM_LEAD`, `EMPLOYEE`.
- **SaaS staff** have no HR permissions, so they cannot read client HR data.
- **Plan limits:** `maxEmployees`, `maxBranches`, and feature gates (PAYROLL, ASSETS). An organization without a
  TRIAL / ACTIVE / GRACE_PERIOD subscription cannot add employees or branches.
