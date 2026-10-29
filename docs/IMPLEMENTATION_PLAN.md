# TraceBack — Implementation Plan & Roadmap

## 1. Project Overview & Identity
- **Title**: TraceBack
- **Subtitle**: Intelligent Lost-and-Found Recovery and Matching Platform
- **Mission**: Connect lost-item reports with found-item reports through MongoDB-first querying, deterministic explainable matching algorithms, geospatial location search, secure ownership verification & claims, and in-app notifications.

---

## 2. Environment & Tooling Verification Status
- **Operating System**: Windows 11 (amd64)
- **Java**: Java 21 (21.0.12 LTS Oracle JDK) — Verified
- **Maven**: Apache Maven 3.9.9 — Verified
- **Node.js**: v24.18.0 — Verified
- **npm**: 11.16.0 — Verified
- **Git**: 2.55.0.windows.3 — Verified
- **Docker CLI**: 29.1.2 installed
- **Database Strategy**:
  - Primary persistent database: MongoDB on port 27017 (Local Service / Atlas / Docker Compose)
  - Integration testing: Hermetic Maven builds with JUnit 5 & Mockito.

---

## 3. Comprehensive Checklist by Stage

### Stage 1: Environment & Implementation Checklist
- [x] Workspace inspection (empty workspace confirmed)
- [x] Tool availability check (Java 21, Maven 3.9, Node 24, npm 11 verified)
- [x] Create `docs/IMPLEMENTATION_PLAN.md`

### Stage 2: Architecture & System Design
- [x] Create `docs/ARCHITECTURE.md` (Monolith modular structure, DTO layer, workflow)
- [x] Create `docs/DATABASE_DESIGN.md` (Collections, 2dsphere indexes, unique/compound indexes, GeoJSON)
- [x] Create `docs/API_SPECIFICATION.md` (RESTful contracts, status codes, DTO schemas)
- [x] Create `docs/SECURITY.md` (JWT token handling, BCrypt hashing, role-based authorization, privacy protections)
- [x] Create `docs/TESTING_PLAN.md` (Unit tests, service tests, controller tests, frontend Vitest)

### Stage 3: Backend Implementation (Spring Boot 3.3.4 + Java 21)
- [x] Initialize `backend/pom.xml` with dependencies:
  - `spring-boot-starter-web`
  - `spring-boot-starter-data-mongodb`
  - `spring-boot-starter-security`
  - `spring-boot-starter-validation`
  - `spring-boot-starter-actuator`
  - `springdoc-openapi-starter-webmvc-ui` (OpenAPI 3 / Swagger)
  - `jjwt-api`, `jjwt-impl`, `jjwt-jackson` (JWT security)
  - `de.flapdoodle.embed.mongo.spring3x`
  - `spring-boot-starter-test` (JUnit 5, Mockito)
- [x] Configure `application.yml` and environment variable bindings.
- [x] Core Common & Error Handling:
  - Custom exceptions (`ResourceNotFoundException`, `UnauthorizedException`, `ConflictException`, `BadRequestException`)
  - Global Exception Handler returning standardized `ApiErrorResponse`
- [x] Security & Auth module:
  - `User` entity, `Role` enum (`ROLE_USER`, `ROLE_MODERATOR`, `ROLE_ADMIN`)
  - JWT Token Provider & Filter
  - Security Config (CORS, CSRF disabled for stateless JWT, authorized matchers)
  - Auth Controller & Service (`/api/auth/register`, `/api/auth/login`, `/api/auth/me`)
- [x] Categories module:
  - Category entity with unique slug index
  - Public listing and Admin CRUD
- [x] Reports module:
  - `ItemReport` entity with GeoJSON `Point`, `ReportType` (LOST, FOUND), `ItemStatus`
  - Geospatial `2dsphere` index on `location`, text indexing on `title` and `description`
  - Approximate public location masking (protecting exact private coordinates)
  - Reports CRUD Controller, Service, and Repository
  - Nearby geospatial query (`GET /api/reports/nearby`)
- [x] Matching Engine module:
  - Deterministic explainable scoring algorithm (0-100 score breakdown: Category, Title/Keywords, Date compatibility, Proximity, Color/Brand)
  - Match repository with compound unique index `[lostReportId, foundReportId]`
  - Match calculation triggered on report creation/update
  - Match Controller & Service (`GET /api/matches/my-reports`, `/api/matches/report/{id}`)
- [x] Claims & Recovery Workflow module:
  - `Claim` entity with private ownership evidence
  - Status transitions (`PENDING` -> `UNDER_REVIEW` -> `APPROVED` / `REJECTED` -> `COMPLETED`)
  - Concurrency safety with atomic Mongo operations
  - Claimant vs Report Owner vs Moderator authorization rules
- [x] Notifications module:
  - `Notification` entity in MongoDB
  - Triggered on match found, claim submitted, claim status changed
  - Read/unread tracking and pagination
- [x] Analytics & Aggregation module:
  - MongoDB Aggregation pipelines: counts by type, status, category, recovery rate, timeline trends
- [x] Audit Log module:
  - Append-only MongoDB collection tracking sensitive security actions
- [x] Seed Data Runner:
  - Automatic initial category creation and demo dataset for testing/demo

### Stage 4: Frontend Implementation (React 18 + TypeScript + Vite + Tailwind CSS)
- [x] Initialize `frontend/` using Vite + React + TypeScript + Tailwind CSS
- [x] Setup Axios/Fetch API client with auth token interceptor and global error handling
- [x] Implement Auth Context (session state, user profile, login, register, logout)
- [x] Design System & Layout:
  - Clean, minimal, professional color scheme: deep navy headers/sidebar, teal/blue buttons, white/light slate background
  - Responsive desktop sidebar and mobile navigation bar
  - Summary cards, badges, status indicators, accessible contrast
- [x] Public Pages:
  - Landing Page (`/`)
  - How It Works (`/how-it-works`)
  - Browse Items (`/browse`) with filters (Lost / Found, Category, Search keyword)
  - Item Detail View (`/reports/:id`)
  - Login (`/login`) & Register (`/register`)
- [x] Authenticated User Pages:
  - User Dashboard (`/dashboard`) with 4 main summary cards + 2 action buttons + recent activity
  - Report Lost Item Form (`/reports/new-lost`) with coordinate picker / location label
  - Report Found Item Form (`/reports/new-found`)
  - My Reports list (`/my-reports`) with edit/delete actions
  - Report Detail & Matches view (`/reports/:id`) with explainable score breakdown
  - Nearby Items Explorer (`/nearby`)
  - Submit Claim Modal / Form (`ClaimModal` component)
  - My Claims (`/my-claims`) tracking status
  - Notifications Center (`/notifications`)
  - User Profile (`/profile`)
- [x] Moderator & Admin Views:
  - Moderator Dashboard (`/moderation`) for reviewing claims with evidence
  - Admin Dashboard (`/admin`) with MongoDB aggregation analytics charts
  - User & Category Management

### Stage 5: End-to-End Workflow & Integration
- [x] Connect all frontend components to live backend endpoints
- [x] Verify complete recovery lifecycle:
  1. User A reports Lost MacBook
  2. User B reports Found MacBook in library
  3. Matching engine scores pair (76.5%), explains match reasons, generates notification
  4. User A reviews match and files ownership claim with private serial number evidence
  5. User B / Moderator reviews claim and approves it
  6. Item marked as RETURNED / COMPLETED and status tracked

### Stage 6: Testing & Quality Assurance
- [x] Backend JUnit 5 unit tests for Matching Engine scoring (PASS)
- [x] Backend tests for Auth, Report CRUD, Geospatial proximity (PASS)
- [x] Frontend Vitest component and form validation tests (PASS)
- [x] Execute `mvn test` and `npm run build` to verify clean build without warnings (PASS)

### Stage 7: Docker, Deployment & Configuration
- [x] Backend `Dockerfile` (multi-stage Maven build with Java 21 runtime)
- [x] Frontend `Dockerfile` (multi-stage Node build with Nginx)
- [x] `compose.yaml` (backend, frontend, mongo service, healthchecks, environment variables)
- [x] `.env.example`
- [x] Cloud Deployment Guide in `README.md`

### Stage 8: Final Verification & Documentation
- [x] Complete `README.md` with architecture diagrams, setup commands, API specs, and verification checklist
- [x] Generate `docs/FINAL_VERIFICATION_REPORT.md`
