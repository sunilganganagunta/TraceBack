# TraceBack — REST API Specification

All API endpoints are prefixed with `/api`.
All responses follow a consistent JSON response envelope or standardized error format.

---

## 1. Standard Error Envelope
```json
{
  "timestamp": "2026-10-29T10:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Title is required and must be at least 3 characters",
  "path": "/api/reports",
  "validationErrors": [
    { "field": "title", "rejectedValue": "", "message": "Title cannot be blank" }
  ]
}
```

---

## 2. Endpoints Overview

### 2.1 Authentication (`/api/auth`)
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `POST` | `/api/auth/register` | Public | Register new user account |
| `POST` | `/api/auth/login` | Public | Authenticate user & return JWT token |
| `GET` | `/api/auth/me` | Authenticated | Retrieve current user profile & roles |

### 2.2 User Profiles (`/api/users`)
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `GET` | `/api/users/me` | Authenticated | Retrieve profile details |
| `PATCH` | `/api/users/me` | Authenticated | Update user name, phone, preferences |
| `GET` | `/api/users/admin/all` | Admin | Paginated list of users |
| `PATCH` | `/api/users/admin/{id}/status` | Admin | Change account status (ACTIVE/SUSPENDED) |

### 2.3 Categories (`/api/categories`)
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `GET` | `/api/categories` | Public | List all active categories |
| `POST` | `/api/categories` | Admin | Create category |
| `PUT` | `/api/categories/{id}` | Admin | Update category |
| `DELETE` | `/api/categories/{id}` | Admin | Deactivate category |

### 2.4 Item Reports (`/api/reports`)
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `POST` | `/api/reports` | Authenticated | Create a new lost or found report |
| `GET` | `/api/reports` | Public | Paginated list of public reports with query filters |
| `GET` | `/api/reports/nearby` | Public | Geospatial search (`longitude`, `latitude`, `radiusMeters`) |
| `GET` | `/api/reports/my-reports` | Authenticated | List all reports belonging to the current user |
| `GET` | `/api/reports/{id}` | Public/Auth | Get report details (privacy-conscious location) |
| `PUT` | `/api/reports/{id}` | Owner/Mod/Admin | Update report details |
| `PATCH` | `/api/reports/{id}/status` | Owner/Mod/Admin | Transition report status (`WITHDRAWN`, etc.) |
| `DELETE` | `/api/reports/{id}` | Owner/Admin | Soft delete / archive report |

### 2.5 Matches (`/api/matches`)
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `GET` | `/api/matches/report/{reportId}` | Owner/Mod/Admin | Get all scored matches for a specific report |
| `GET` | `/api/matches/my-matches` | Authenticated | List top matches across all user's reports |
| `POST` | `/api/matches/recalculate/{reportId}` | Mod/Admin | Force recalculation of matches |

### 2.6 Claims (`/api/claims`)
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `POST` | `/api/claims` | Authenticated | Submit an ownership verification claim |
| `GET` | `/api/claims/my-claims` | Authenticated | List claims submitted by the authenticated user |
| `GET` | `/api/claims/report/{reportId}` | Report Owner/Mod | List claims submitted against user's found item |
| `GET` | `/api/claims/{id}` | Claimant/Owner/Mod | View claim details (private evidence restricted) |
| `PATCH` | `/api/claims/{id}/status` | Owner/Mod/Admin | Update claim status (`APPROVED`, `REJECTED`, etc.) |

### 2.7 Notifications (`/api/notifications`)
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `GET` | `/api/notifications` | Authenticated | Paginated notifications for current user |
| `GET` | `/api/notifications/unread-count` | Authenticated | Total unread notification count |
| `PATCH` | `/api/notifications/{id}/read` | Authenticated | Mark a notification as read |
| `PATCH` | `/api/notifications/mark-all-read` | Authenticated | Mark all notifications as read |

### 2.8 Analytics (`/api/analytics`)
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `GET` | `/api/analytics/summary` | Admin/Mod | Aggregated platform statistics (totals, recovery rate) |
| `GET` | `/api/analytics/categories` | Admin/Mod | Reports distribution by category |
| `GET` | `/api/analytics/timeline` | Admin/Mod | Monthly recovery & submission trends |

### 2.9 Health & System
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `GET` | `/actuator/health` | Public | Safe health status (`UP`) |
| `GET` | `/swagger-ui.html` | Public | Interactive OpenAPI Documentation |
