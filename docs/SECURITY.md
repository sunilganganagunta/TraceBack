# TraceBack — Application Security Architecture

## 1. Authentication & JWT Strategy
1. **Stateless JWT Tokens**:
   - Authentication tokens are issued upon successful credential verification (`POST /api/auth/login`).
   - Token payload includes Subject (`userId`), `email`, and `roles` claims.
   - Tokens are signed with HMAC-SHA256 (`HS256`) using a cryptographically secure 256-bit secret configured via `JWT_SECRET`.
   - Default expiration is 24 hours (configurable).
2. **Password Storage**:
   - Stored in `passwordHash` using BCrypt (work factor 10).
   - Plaintext passwords never logged, persisted, or returned in DTOs.
3. **Email Normalization**:
   - Incoming emails are trimmed and lowercased (`normalizedEmail`) to prevent case-variation account takeover or duplication.

---

## 2. Authorization & RBAC
- Three distinct roles:
  - `ROLE_USER`: Standard registered user (create/edit own reports, file claims, view matches).
  - `ROLE_MODERATOR`: Elevated review capability (review dispute claims, inspect content flags).
  - `ROLE_ADMIN`: Full system administrative control (analytics, category management, account suspension).
- Method-level and Spring Security Filter Chain URL authorization enforced.
- Ownership Verification: Service layer enforces that only report owners or admins can edit/delete reports or view restricted private claim verification evidence.

---

## 3. Privacy Preservation & Data Protection
1. **Location Privacy**:
   - Exact latitude and longitude coordinates stored as GeoJSON in MongoDB are used for distance calculations, but the public API responses provide `publicLocationLabel` (e.g. "Near Campus Dining Hall") and approximate coordinates rounded to protect user residential privacy.
2. **Private Verification Evidence**:
   - When filing a claim for a found item, private serial numbers or hidden identifying marks are stored in a restricted embedded object (`privateVerificationEvidence`).
   - The API explicitly strips this evidence from all public listings, match results, and competitor claimant queries. Only the report owner (or assigned moderator) can view evidence during the review phase.
3. **No Sensitive Defaults**:
   - No hardcoded administrator accounts with default credentials.
   - All secrets injected via `.env` / environment variables.

---

## 4. Web & Network Security Controls
- **CORS**: Explicit allowed origins configured via `FRONTEND_ORIGIN` (default `http://localhost:5173`).
- **Security Headers**: Standard OWASP headers configured (Content-Security-Policy, X-Frame-Options, X-Content-Type-Options).
- **Safe Error Responses**: Stack traces disabled in production profiles; generic error codes returned to clients.
- **Input Validation**: Jakarta Bean Validation (`@NotBlank`, `@Size`, `@Email`, `@Min`, `@Max`) on all request DTOs.
