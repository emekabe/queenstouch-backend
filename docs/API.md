# API Reference — Queenstouch Backend

All responses use the standard envelope:
```json
{ "success": true, "message": "...", "data": { ... }, "timestamp": "..." }
```

---

## Auth (`/api/v1/auth`)

| Method | Path | Auth | Description |
|---|---|---|---|
| POST | `/register` | Public | Register new user |
| POST | `/verify-email` | Public | Verify email with 6-digit OTP |
| POST | `/resend-otp` | Public | Resend verification OTP |
| POST | `/login` | Public | Login → access + refresh tokens |
| POST | `/refresh` | Public | Refresh access token |
| POST | `/forgot-password` | Public | Send password-reset OTP |
| POST | `/reset-password` | Public | Reset password with OTP |

---

## User (`/api/v1/users`)

| Method | Path | Auth | Description |
|---|---|---|---|
| GET | `/me` | Bearer | Get current user profile |
| PUT | `/me` | Bearer | Update profile |
| PUT | `/me/change-password` | Bearer | Change password |

---

## CV Builder (`/api/v1/cvs`)

| Method | Path | Auth | Description |
|---|---|---|---|
| POST | `/` | Bearer | Create new CV draft |
| GET | `/` | Bearer | List all CVs |
| GET | `/{id}` | Bearer | Get CV by ID |
| PUT | `/{id}` | Bearer | Full CV update (any fields) |
| PATCH | `/{id}/personal-info` | Bearer | Update personal info section |
| PATCH | `/{id}/experience` | Bearer | Update work experience section |
| PATCH | `/{id}/education` | Bearer | Update education section |
| PATCH | `/{id}/skills` | Bearer | Update skills section |
| PATCH | `/{id}/certifications` | Bearer | Update certifications section |
| PATCH | `/{id}/academic-sections` | Bearer | Update academic-only sections |
| DELETE | `/{id}` | Bearer | Delete CV |
| POST | `/{id}/generate-summary` | Bearer | **AI**: Generate professional summary |
| POST | `/generate-achievement` | Bearer | **AI**: Achievement bullet builder |
| POST | `/{id}/score` | Bearer | **AI**: Compute CV Strength Score |
| POST | `/{id}/job-match` | Bearer | **AI**: Match CV vs job description |

---

## Cover Letter (`/api/v1/cover-letters`)

| Method | Path | Auth | Description |
|---|---|---|---|
| POST | `/` | Bearer | Generate cover letter (AI) |
| GET | `/` | Bearer | List all cover letters |
| GET | `/{id}` | Bearer | Get cover letter |
| DELETE | `/{id}` | Bearer | Delete cover letter |

---

## LinkedIn (`/api/v1/linkedin`)

| Method | Path | Auth | Description |
|---|---|---|---|
| POST | `/generate` | Bearer | Generate LinkedIn headline + summary + skills (AI) |
| GET | `/` | Bearer | List profiles |
| GET | `/{id}` | Bearer | Get profile |
| DELETE | `/{id}` | Bearer | Delete profile |

---

## Orders & Pricing (`/api/v1/orders`)

| Method | Path | Auth | Description |
|---|---|---|---|
| GET | `/pricing` | Public | Full pricing catalogue |
| POST | `/` | Bearer | Create order (mock: auto-PAID) |
| GET | `/` | Bearer | List orders |
| GET | `/{id}` | Bearer | Get order |
| POST | `/webhook/payment` | Public | Payment webhook stub (no-op) |

---

## Premium Requests (`/api/v1/premium-requests`)

| Method | Path | Auth | Description |
|---|---|---|---|
| POST | `/` | Bearer | Submit a premium service request |
| GET | `/` | Bearer | List your requests |
| GET | `/{id}` | Bearer | Get request detail |
| POST | `/{id}/upload` | Bearer | Upload supporting file (multipart) |

---

## Admin (`/api/v1/admin`) — ADMIN role required

| Method | Path | Auth | Description |
|---|---|---|---|
| GET | `/users` | Admin | List all users |
| GET | `/stats` | Admin | Platform stats |
| GET | `/orders` | Admin | List all orders |
| GET | `/premium-requests` | Admin | List all premium requests |
| PUT | `/premium-requests/{id}/status` | Admin | Update request status + notes |
