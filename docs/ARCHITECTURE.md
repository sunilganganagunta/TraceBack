# TraceBack — System Architecture Document

## 1. Executive Summary
TraceBack is an intelligent, privacy-centric lost-and-found recovery platform designed as a modular monolith. It leverages Spring Boot 3 on Java 21 for secure business logic orchestration and MongoDB as a high-performance document database handling geospatial queries, compound indexing, and real-time aggregation analytics.

---

## 2. High-Level Architecture Diagram

```
+------------------------------------------------------------------------+
|                      React 19 + TypeScript + Vite                      |
| (Responsive SPA, Tailwind CSS, Lucide Icons, Axios API Client, Zod)    |
+-----------------------------------+------------------------------------+
                                    | HTTPS / JSON (JWT in Bearer Header)
                                    v
+------------------------------------------------------------------------+
|                  TraceBack Spring Boot 3.3.x Backend                   |
|                                                                        |
|  +------------------------+  +---------------------------------------+ |
|  | Web Layer (REST)       |  | Security Layer                        | |
|  | - AuthController       |  | - JwtAuthenticationFilter             | |
|  | - ReportController     |  | - SecurityFilterChain (CORS/CSRF)     | |
|  | - ClaimController      |  | - Role-based authorization            | |
|  | - MatchController      |  +---------------------------------------+ |
|  | - NotificationCtrl     |                                            |
|  | - AnalyticsController  |  +---------------------------------------+ |
|  | - AdminController      |  | Exception Handling                    | |
|  +-----------+------------+  | - GlobalExceptionHandler              | |
|              |               +---------------------------------------+ |
|              v                                                         |
|  +-------------------------------------------------------------------+ |
|  | Service Layer (Business Logic & Orchestration)                    | |
|  | - AuthService, UserService                                        | |
|  | - ReportService (Geospatial search & privacy masking)             | |
|  | - MatchingEngineService (Deterministic 0-100 scoring algorithm)   | |
|  | - ClaimService (Atomic transitions & ownership evidence checks)   | |
|  | - NotificationService, AnalyticsService, AuditLogService          | |
|  +-----------+-------------------------------------------------------+ |
|              |                                                         |
|              v                                                         |
|  +-------------------------------------------------------------------+ |
|  | Persistence Layer (Spring Data MongoDB & MongoTemplate)           | |
|  | - Repositories: User, ItemReport, Claim, Match, Notification, etc.| |
|  | - MongoTemplate: Custom Aggregations & Conditional Atomic Updates | |
|  +-------------------------------------------------------------------+ |
+-----------------------------------+------------------------------------+
                                    | Mongo Wire Protocol
                                    v
+------------------------------------------------------------------------+
|                          MongoDB Database                              |
|  Collections: users, item_reports, claims, matches, notifications,    |
|               categories, audit_logs                                   |
|  Indexes: 2dsphere (geospatial), unique normalized email/slugs,        |
|           compound [lostReportId, foundReportId], status & date ranges |
+------------------------------------------------------------------------+
```

---

## 3. Package Structure (Backend)

The backend follows a domain-driven modular monolith architecture under `com.traceback`:
- `config`: Application configuration, Mongo configuration, OpenAPI/Swagger doc settings, WebMvc CORS settings.
- `security`: JWT token generation, parsing, validation, filter chain, user details service, password encryption.
- `common`: Global exception handling, standardized API response envelopes, pagination DTOs, base entities.
- `users`: User entity, DTOs, repository, service, and controller (`/api/users`).
- `auth`: Registration, login, password validation, token issuing (`/api/auth`).
- `categories`: Category taxonomy management (`/api/categories`).
- `reports`: Lost and found reports, GeoJSON location coordinates, approximate public labeling, repository with `2dsphere` index, search and filtering (`/api/reports`).
- `matching`: Intelligent deterministic matching engine, signal scoring (category, keywords, dates, distance), match reasons generator, repository with unique compound pair index (`/api/matches`).
- `claims`: Recovery claims, restricted private verification evidence, status state-machine (`/api/claims`).
- `notifications`: In-app notification creation, unread count, read tracking (`/api/notifications`).
- `analytics`: MongoDB Aggregation pipelines for platform metrics and recovery rates (`/api/analytics`).
- `audit`: Append-only audit trail for sensitive administrative and moderation events.

---

## 4. Frontend Architecture (React + TypeScript)

The client is structured cleanly under `frontend/src`:
- `api`: Typed Axios client with request/response interceptors injecting JWT tokens and handling 401/403 responses gracefully.
- `context`: `AuthContext` providing user authentication state, token persistence, login/logout functions.
- `components`:
  - `layout`: Header, Sidebar, Footer, MobileNav, ProtectedRoute.
  - `ui`: Reusable styled primitives (Button, Input, Card, Badge, Modal, Spinner, Alert).
  - `feedback`: Toast alerts, empty state illustrations, confirmation dialogs.
- `features`:
  - `auth`: Login, Register, Profile cards.
  - `reports`: ReportCard, ReportForm (Lost & Found), ReportFilters, LocationPicker.
  - `matches`: MatchCard with score gauge (0-100) and bulleted explainable match reasons.
  - `claims`: ClaimForm with private evidence fields, ClaimStatusBadge, ClaimReviewCard.
  - `notifications`: NotificationBell, NotificationList.
  - `analytics`: StatCard, CategoryBreakdown, RecoveryRateChart.
- `pages`: Public landing, browse, details, user dashboard, my reports, my claims, notifications, moderator dashboard, admin dashboard.
- `types`: TypeScript interfaces reflecting backend DTOs.
