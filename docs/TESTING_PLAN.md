# TraceBack — Comprehensive Testing Plan

## 1. Scope & Strategy
TraceBack employs a multi-tiered testing strategy:
1. **Unit Tests (Backend)**: Fast, isolated JUnit 5 & Mockito tests validating business logic, DTO mapping, and specifically the deterministic Matching Engine scoring algorithm.
2. **Repository & Query Tests (Backend)**: Testing Spring Data MongoDB queries, index queries, and geospatial distance logic using embedded MongoDB / mock Mongo.
3. **Controller & Security Tests (Backend)**: Testing REST API contracts, validation constraint errors, JWT authentication filters, and role-based permissions using `MockMvc`.
4. **Frontend Unit & Component Tests**: Vitest + React Testing Library tests for form validations, auth state, and match cards.
5. **End-to-End Workflow Verification**: Automated script / verification running registration -> report creation -> match computation -> claim filing -> status progression.

---

## 2. Test Suites Overview

### 2.1 Backend Tests
- `MatchingEngineServiceTest`: Tests scoring weights (category match = 30 pts, keyword title match = 25 pts, date compatibility = 15 pts, proximity = 15 pts, color/brand = 15 pts). Boundary cases: 0 score when category incompatible, date compatibility penalties when found before lost date.
- `AuthServiceTest`: Tests registration, duplicate email rejection (409 Conflict), password hashing, successful JWT generation on valid login, bad credential handling (401 Unauthorized).
- `ReportServiceTest`: Tests report creation, GeoJSON point assignment, update permissions, soft delete/archival.
- `ClaimServiceTest`: Tests claim creation on valid reports, status transitions (`PENDING` -> `APPROVED`), authorization boundary ensuring non-owners cannot approve claims.
- `NotificationServiceTest`: Tests unread count calculation and mark-as-read updates.
- `AnalyticsServiceTest`: Tests MongoDB aggregation results formatting and zero-division protection.

### 2.2 Frontend Tests
- `LoginForm.test.tsx`: Tests email and password input validations, submission handler, error display.
- `ReportForm.test.tsx`: Tests required fields, category selection, and coordinate inputs.
- `MatchCard.test.tsx`: Tests rendering of similarity score progress bar and explainable reasons list.
