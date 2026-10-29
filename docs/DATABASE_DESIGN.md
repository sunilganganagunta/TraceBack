# TraceBack — MongoDB Database Design Document

## 1. Design Principles & Goals
TraceBack uses MongoDB as its primary persistence and processing engine. MongoDB-native capabilities used include:
1. **Document Embedding vs. Referencing**: Referencing independent lifecycle entities (Users, ItemReports, Claims, Matches) while embedding sub-documents (GeoJSON Points, ScoreBreakdowns, PrivacyPreferences).
2. **Geospatial Proximity**: Native GeoJSON `Point` coordinates indexed with `2dsphere` for `$near` proximity queries.
3. **Compound & Unique Indexes**: Ensuring zero duplicate accounts, unique category slugs, and deduplicated directional match pairs `[lostReportId, foundReportId]`.
4. **Aggregation Pipelines**: Real-time statistical computation for recovery rates, category trends, and moderation volume.
5. **Atomic Operations**: Safe status transitions using MongoTemplate atomic conditional queries (`findAndModify` / `updateFirst`).

---

## 2. Collections & Schema Definitions

### 2.1 Collection: `users`
Stores user profile, authentication credentials, and authorization roles.
```json
{
  "_id": "ObjectId",
  "name": "string (2-100 chars)",
  "email": "string (RFC 5322)",
  "normalizedEmail": "string (lowercase, trimmed)",
  "passwordHash": "string (BCrypt 10 rounds)",
  "roles": ["ROLE_USER", "ROLE_MODERATOR", "ROLE_ADMIN"],
  "accountStatus": "ACTIVE | SUSPENDED",
  "emailVerified": "boolean",
  "phone": "string (optional)",
  "profileImage": "string (optional URL)",
  "privacyPreferences": {
    "showEmailToClaimants": false,
    "showPhoneToClaimants": false
  },
  "createdAt": "ISODate",
  "updatedAt": "ISODate",
  "lastLoginAt": "ISODate"
}
```
**Indexes**:
- `{ "normalizedEmail": 1 }` (UNIQUE)
- `{ "roles": 1 }`

---

### 2.2 Collection: `categories`
Hierarchical taxonomy for items (Electronics, Bags & Wallets, Keys, Documents, Clothing, Jewelry, Books, Other).
```json
{
  "_id": "ObjectId",
  "name": "string",
  "slug": "string",
  "description": "string",
  "icon": "string",
  "active": true,
  "createdAt": "ISODate",
  "updatedAt": "ISODate"
}
```
**Indexes**:
- `{ "slug": 1 }` (UNIQUE)
- `{ "active": 1 }`

---

### 2.3 Collection: `item_reports`
The core document storing lost and found items.
```json
{
  "_id": "ObjectId",
  "ownerId": "ObjectId (ref: users)",
  "reportType": "LOST | FOUND",
  "title": "string (3-120 chars)",
  "categoryId": "string (ref: categories)",
  "categoryName": "string (denormalized for fast display)",
  "description": "string (max 2000 chars)",
  "color": "string",
  "brand": "string (optional)",
  "distinguishingFeatures": "string (optional)",
  "dateLostOrFound": "ISODate",
  "location": {
    "type": "Point",
    "coordinates": [77.5946, 12.9716]  // [longitude, latitude] GeoJSON standard
  },
  "publicLocationLabel": "string (e.g. 'Near Central Library, North Block')",
  "images": ["string URL"],
  "status": "ACTIVE | MATCHED | CLAIM_PENDING | RETURNED | WITHDRAWN | CLOSED",
  "visibility": "PUBLIC | RESTRICTED",
  "createdAt": "ISODate",
  "updatedAt": "ISODate",
  "version": 0
}
```
**Indexes**:
- `{ "location": "2dsphere" }` (Geospatial proximity search)
- `{ "reportType": 1, "status": 1, "categoryId": 1 }` (High-efficiency match filtering)
- `{ "ownerId": 1, "createdAt": -1 }` (User dashboard listing)
- `{ "title": "text", "description": "text", "brand": "text" }` (Text search index)

---

### 2.4 Collection: `claims`
Ownership verification claims submitted by users for found items or disputed reports.
```json
{
  "_id": "ObjectId",
  "reportId": "ObjectId (ref: item_reports)",
  "claimantId": "ObjectId (ref: users)",
  "claimantName": "string",
  "claimDescription": "string (10-1000 chars)",
  "privateVerificationEvidence": {
    "identifyingMarks": "string",
    "serialNumber": "string",
    "receiptOrProofUrl": "string",
    "additionalSecretDetails": "string"
  },
  "status": "PENDING | UNDER_REVIEW | APPROVED | REJECTED | WITHDRAWN | COMPLETED",
  "reviewerId": "ObjectId (optional, ref: users)",
  "reviewNotes": "string (optional)",
  "createdAt": "ISODate",
  "updatedAt": "ISODate",
  "resolvedAt": "ISODate"
}
```
**Indexes**:
- `{ "reportId": 1, "claimantId": 1 }` (Fast lookups per report/claimant)
- `{ "claimantId": 1, "status": 1 }` (Claimant history)
- `{ "status": 1, "createdAt": -1 }` (Moderator review queues)

---

### 2.5 Collection: `matches`
Computed similarity pairs between a `LOST` report and a `FOUND` report.
```json
{
  "_id": "ObjectId",
  "lostReportId": "ObjectId (ref: item_reports)",
  "foundReportId": "ObjectId (ref: item_reports)",
  "score": 85.5,
  "scoreBreakdown": {
    "categoryScore": 30.0,
    "titleKeywordScore": 25.0,
    "dateScore": 15.0,
    "locationProximityScore": 10.5,
    "attributeScore": 5.0
  },
  "reasons": [
    "Category matches: Electronics",
    "High title similarity: 'Apple MacBook Pro M2'",
    "Date compatibility: Lost Nov 2nd, Found Nov 3rd",
    "Location proximity: Within 450 meters"
  ],
  "status": "ACTIVE | DISMISSED | CONFIRMED",
  "createdAt": "ISODate",
  "updatedAt": "ISODate",
  "lastCalculatedAt": "ISODate"
}
```
**Indexes**:
- `{ "lostReportId": 1, "foundReportId": 1 }` (UNIQUE compound index, prevents reverse or duplicate pairings)
- `{ "lostReportId": 1, "score": -1 }` (Top matches for a lost report)
- `{ "foundReportId": 1, "score": -1 }` (Top matches for a found report)

---

### 2.6 Collection: `notifications`
In-app notification records for users.
```json
{
  "_id": "ObjectId",
  "recipientId": "ObjectId (ref: users)",
  "type": "MATCH_FOUND | CLAIM_SUBMITTED | CLAIM_STATUS_UPDATED | SYSTEM_ALERT",
  "title": "string",
  "message": "string",
  "relatedResourceId": "string",
  "relatedResourceType": "REPORT | CLAIM | MATCH",
  "read": false,
  "createdAt": "ISODate",
  "readAt": "ISODate"
}
```
**Indexes**:
- `{ "recipientId": 1, "read": 1, "createdAt": -1 }` (Unread notifications query)

---

### 2.7 Collection: `audit_logs`
Append-only log for compliance, role modifications, and moderation decisions.
```json
{
  "_id": "ObjectId",
  "actorId": "ObjectId (ref: users)",
  "actorEmail": "string",
  "action": "string (e.g. 'CLAIM_APPROVED', 'REPORT_WITHDRAWN')",
  "resourceType": "REPORT | CLAIM | USER",
  "resourceId": "string",
  "timestamp": "ISODate",
  "safeMetadata": {
    "reason": "string",
    "ipAddress": "string"
  }
}
```
**Indexes**:
- `{ "timestamp": -1 }`
- `{ "resourceType": 1, "resourceId": 1 }`
