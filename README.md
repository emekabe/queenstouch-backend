# Queenstouch Global CV Builder — Backend

A production-ready Spring Boot 3 REST API powering the Queenstouch CV & Career Tools platform.

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.x |
| Database | MongoDB (Spring Data MongoDB) |
| AI | Spring AI → Google Gemini |
| Auth | JWT (access + refresh tokens) |
| Docs | springdoc-openapi (Swagger UI) |
| Build | Gradle |

---

## Quick Start

### 1. Prerequisites

- Java 21+
- MongoDB running locally on `mongodb://localhost:27017` (or set `MONGODB_URI`)
- Google Gemini API key (optional — a mock AI service is provided)

### 2. Environment Variables

Copy `.env.example` to `.env` (or export in shell):

```bash
MONGODB_URI=mongodb://localhost:27017/queenstouch
JWT_SECRET=your-256-bit-secret-here
GEMINI_API_KEY=your-google-ai-studio-key
MAIL_HOST=smtp.example.com
MAIL_PORT=587
MAIL_USERNAME=noreply@queenstouch.com
MAIL_PASSWORD=yourpassword
```

### 3. Run

```bash
./gradlew bootRun
```

API available at: `http://localhost:8080`  
Swagger UI at: `http://localhost:8080/swagger-ui.html`

---

## Project Structure

```
src/main/java/com/queenstouch/queenstouchbackend/
├── config/           # Spring config (Security, OpenAPI, AppProperties)
├── controller/       # REST controllers
├── dto/              # Request / Response DTOs
├── exception/        # GlobalExceptionHandler + AppException
├── model/            # MongoDB document models + enums
├── repository/       # Spring Data MongoDB repositories
├── service/          # Business logic services
│   └── ai/          # AiService interface + GeminiAiService + MockAiService
└── util/             # OtpUtil, JwtUtil
```

---

## API Overview

See `docs/API.md` for the full endpoint listing or browse **Swagger UI** at runtime.

| Tag | Base Path | Description |
|---|---|---|
| Auth | `/api/v1/auth` | Register, verify email, login, refresh, reset password |
| User | `/api/v1/users` | Profile read & update |
| CV Builder | `/api/v1/cvs` | Full CV lifecycle + AI features |
| Cover Letter | `/api/v1/cover-letters` | AI cover letter generation |
| LinkedIn | `/api/v1/linkedin` | AI LinkedIn profile generation |
| Orders & Pricing | `/api/v1/orders` | Mocked payment & pricing catalogue |
| Premium Requests | `/api/v1/premium-requests` | Human-expert service requests |
| Admin | `/api/v1/admin` | Platform stats, user/order management |

---

## AI Feature Notes

All AI features call **Spring AI → Google Gemini Flash** by default.  
If `GEMINI_API_KEY` is not set, the `MockAiService` bean provides realistic stub responses automatically — no config needed for local dev.

See `docs/AI_INTEGRATION.md` for details.

---

## Pricing

All prices are in **Nigerian Naira (NGN)** and are configured in `application.yml`.
The pricing catalogue is publicly exposed at `GET /api/v1/orders/pricing`.

See `docs/PRICING.md` for the full catalogue.
