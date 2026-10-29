# TraceBack — Intelligent Lost-and-Found Recovery and Matching Platform

> **A secure, privacy-first, full-stack community platform connecting lost belongings with found item reports using MongoDB geospatial queries, deterministic explainable matching algorithms, and confidential ownership verification.**

[![Java 21](https://img.shields.io/badge/Java-21-blue.svg)](https://www.oracle.com/java/)
[![Spring Boot 3.3.4](https://img.shields.io/badge/Spring_Boot-3.3.4-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![MongoDB](https://img.shields.io/badge/MongoDB-7.0-green.svg)](https://www.mongodb.com/)
[![React 18](https://img.shields.io/badge/React-18.3-cyan.svg)](https://react.dev/)
[![TypeScript](https://img.shields.io/badge/TypeScript-5.7-blue.svg)](https://www.typescriptlang.org/)
[![Tailwind CSS](https://img.shields.io/badge/Tailwind-3.4-38bdf8.svg)](https://tailwindcss.com/)

---

## 1. Project Overview & Problem Statement

Misplaced and lost items in colleges, workplaces, transit hubs, and public spaces often go unrecovered due to fragmented physical lost-and-found desks, lack of searchability, and absence of automated notifications. Existing bulletin boards also expose sensitive owner details publicly.

**TraceBack** solves this by providing:
1. **Intelligent, Explainable Matching**: Multi-signal scoring evaluates category compatibility, title tokens, description semantics, color/brand attributes, dates, and geospatial distance to calculate a similarity score (0–100) with bulleted explanations.
2. **Location-Aware Proximity Search**: MongoDB native `2dsphere` spherical indexing finds items nearby while masking exact coordinates to protect personal residence privacy.
3. **Confidential Ownership Claims**: Claimants submit serial numbers, secret marks, and proof of purchase into an encrypted private evidence field accessible only to the item reporter or assigned moderator.
4. **Real-Time Aggregation Analytics**: MongoDB aggregation pipelines deliver live statistics on recovery rates, category distributions, and state-machine workloads.

---

## 2. System Architecture

```
+------------------------------------------------------------------------+
|                     React 18 + TypeScript + Vite                       |
|   (Responsive SPA, Tailwind CSS, Lucide Icons, Axios API Client)       |
+-----------------------------------+------------------------------------+
                                    | HTTPS / JSON (JWT in Bearer Header)
                                    v
+------------------------------------------------------------------------+
|                   Spring Boot 3.3.x Backend (Java 21)                  |
|                                                                        |
|  Controllers  <->  Services  <->  Repositories  <->  MongoTemplate     |
|   - Auth            - Auth          - User             - Aggregations  |
|   - Reports         - Reports       - ItemReport       - 2dsphere Geo  |
|   - Claims          - Matching      - Claim            - Atomic State  |
|   - Matches         - Claims        - Match                            |
|   - Analytics       - Notifications - Notification                     |
+-----------------------------------+------------------------------------+
                                    | Mongo Wire Protocol
                                    v
+------------------------------------------------------------------------+
|                          MongoDB Database                              |
| Collections: users, item_reports, claims, matches, notifications,      |
|              categories, audit_logs                                    |
| Indexes: 2dsphere, unique compound pairs, text search, normalized email|
+------------------------------------------------------------------------+
```

---

## 3. Technology Stack

### Backend
- **Language**: Java 21 LTS
- **Framework**: Spring Boot 3.3.4
- **Persistence**: Spring Data MongoDB & `MongoTemplate`
- **Security**: Spring Security 6 with stateless JWT (`io.jsonwebtoken:jjwt`) and BCrypt password encryption
- **API Documentation**: Springdoc OpenAPI 2.6.0 (Swagger UI)
- **Validation**: Jakarta Bean Validation (`@Valid`, `@NotBlank`, `@Size`, `@PastOrPresent`)
- **Testing**: JUnit 5, Mockito, Spring Boot Test

### Database (MongoDB-First Architecture)
- **Geospatial Index**: `2dsphere` index on `ItemReport.location` supporting `$nearSphere` queries
- **Compound Unique Index**: `{ lostReportId: 1, foundReportId: 1 }` on `matches` preventing duplicate pairing
- **Text & Field Indexes**: Compound index on `{ reportType: 1, status: 1, categoryId: 1 }`, text index on titles & descriptions
- **Real-Time Aggregations**: `$group`, `$project`, `$sort`, and `$dateToString` pipelines powering analytics

### Frontend
- **Framework**: React 18 + TypeScript + Vite
- **Styling**: Tailwind CSS (clean navy/teal/white aesthetic)
- **Routing**: React Router v6 with `ProtectedRoute` guards
- **API Client**: Axios with request/response JWT interceptors
- **Icons**: Lucide React
- **Testing**: Vitest + React Testing Library

---

## 4. Multi-Signal Matching Engine

The deterministic scoring algorithm evaluates opposing `LOST` and `FOUND` reports:

| Signal | Maximum Points | Evaluation Method |
|---|---|---|
| **Category Compatibility** | 30 pts | Exact category taxonomy match |
| **Title Keywords** | 25 pts | Jaccard token overlap on title words |
| **Description Overlap** | 15 pts | Term frequency and contextual overlap |
| **Color & Brand** | 10 pts | Exact or fuzzy match on item color (5 pts) and brand (5 pts) |
| **Date Logic** | 10 pts | Date lost $\le$ Date found, decaying gracefully over 30 days |
| **Geospatial Distance** | 15 pts | Haversine formula: $\le 500\text{ m}$ (15 pts), $\le 2\text{ km}$ (10 pts), $\le 10\text{ km}$ (5 pts) |
| **Total** | **100 pts** | Score $\ge 35\%$ saved; score $\ge 55\%$ triggers in-app notification |

> **Disclaimer**: The compatibility score indicates attribute similarity and does not constitute legal proof of ownership.

---

## 5. Getting Started & Installation

### Prerequisites
- **Java 21** (`java -version`)
- **Maven 3.8+** (`mvn -version`)
- **Node.js 20+** and **npm 10+** (`node -v`, `npm -v`)
- **MongoDB** (Local Community Server on port 27017 or a free [MongoDB Atlas](https://www.mongodb.com/cloud/atlas) cluster)

---

### Option A: Running with Docker Compose (Recommended)

1. Clone or navigate to the repository directory:
   ```bash
   cd TraceBack
   ```
2. Start all services (MongoDB, Spring Boot backend, and React frontend):
   ```bash
   docker compose up --build -d
   ```
3. Access the services:
   - **Frontend Web App**: [http://localhost:5173](http://localhost:5173)
   - **Backend API & Health**: [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)
   - **Interactive Swagger Docs**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

---

### Option B: Running Locally for Development

#### 1. Configure Environment
Copy `.env.example` to `.env`:
```bash
cp .env.example .env
```
Ensure MongoDB is running locally at `mongodb://localhost:27017/traceback` or paste your MongoDB Atlas URI into `SPRING_DATA_MONGODB_URI`.

#### 2. Start the Backend (Spring Boot)
```bash
cd backend
mvn clean spring-boot:run
```
The backend starts on `http://localhost:8080`.

#### 3. Start the Frontend (Vite)
In a second terminal:
```bash
cd frontend
npm install
npm run dev
```
The frontend dev server opens on `http://localhost:5173`.

---

## 6. Pre-Seeded Demo Accounts

When starting the application, the database automatically populates categories and 3 showcase user accounts for testing:

| Role | Email | Password | Permissions & Dashboard |
|---|---|---|---|
| **Regular User** | `alex@traceback.demo` | `Password123!` | Create reports, view matches, submit recovery claims |
| **Moderator** | `sam@traceback.demo` | `Password123!` | Review claims, inspect confidential evidence, approve/reject |
| **Administrator** | `admin@traceback.demo` | `AdminSecret123!` | Aggregation analytics, category management, user suspension |

*(Note: The login page includes one-click demo credentials buttons for easy portfolio demonstrations).*

---

## 7. Automated Testing

### Backend Unit & Integration Tests (JUnit 5 + Mockito)
```bash
cd backend
mvn test
```
*Executes matching scoring algorithm tests, auth registration/conflict tests, and claim workflow boundary tests.*

### Frontend Tests (Vitest)
```bash
cd frontend
npm run test
```

### Production Build Verification
```bash
cd frontend
npm run build
```

---

## 8. REST API Summary

Interactive documentation and schemas are available at `/swagger-ui.html`.

- **Authentication**: `POST /api/auth/register`, `POST /api/auth/login`, `GET /api/auth/me`
- **Item Reports**: `GET /api/reports`, `POST /api/reports`, `GET /api/reports/nearby`, `GET /api/reports/{id}`, `DELETE /api/reports/{id}`
- **Matching Engine**: `GET /api/matches/report/{id}`, `GET /api/matches/my-matches`, `POST /api/matches/recalculate/{id}`
- **Recovery Claims**: `POST /api/claims`, `GET /api/claims/my-claims`, `GET /api/claims/report/{id}`, `PATCH /api/claims/{id}/status`
- **In-App Notifications**: `GET /api/notifications`, `GET /api/notifications/unread-count`, `PATCH /api/notifications/{id}/read`
- **Analytics & Aggregations**: `GET /api/analytics/summary`, `GET /api/analytics/categories`, `GET /api/analytics/timeline`
- **User Management**: `GET /api/users/me`, `PATCH /api/users/admin/{id}/status`

---

## 9. License & Academic Attribution
Created for academic portfolio and software engineering placement demonstrations. Released under the [Apache 2.0 License](LICENSE).
