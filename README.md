# Queenstouch Global Career Platform — Backend API

🚀 **A Production-Ready Spring Boot REST API for AI-Powered Career Services.**

This backend provides the engine for CV building, cover letter generation, and LinkedIn optimization, integrated with Google Gemini AI and Google Cloud Storage.

---

## 🛠 Tech Stack

| Layer | Technology |
|---|---|
| **Language** | Java 21 (LTS) |
| **Framework** | Spring Boot 3.4.5 |
| **Build & Dependency** | Gradle (Kotlin DSL compatible) |
| **Database** | MongoDB (Spring Data MongoDB) |
| **AI Integration** | Spring AI (Prompt Engineering + Gemini 2.0 Flash) |
| **Security** | Spring Security + JWT (Stateless) |
| **Storage** | Google Cloud Storage (Bucket integration) |
| **Documents** | iText 7 (PDF) & Apache POI (Word/DOCX) |
| **API Docs** | Springdoc OpenAPI (Swagger UI) |

---

## ✨ Key AI Features

The backend implements advanced AI workflows using **Spring AI → Google Gemini**:

- **Achievement Rewriting**: Uses context-aware prompts to rewrite basic tasks into high-impact bullet points.
- **CV Scoring Engine**: Analyzes CV data against industry standards to provide an impact score (0-100).
- **Job Match Analysis**: Dynamically compares a user's CV against a job description to identify keyword gaps and match percentage.
- **Cover Letter Generation**: Synthesizes CV data and job requirements into a professional, tailored cover letter.
- **Fail-safe Mocking**: Provides a `MockAiService` that activates automatically if no API key is set, ensuring seamless local development.

---

## 🚀 Quick Start

### 1. Prerequisites
- **JDK 21** installed.
- **MongoDB** (local or remote).
- **Gradle** 8 (wrapper included).

### 2. Configuration
Copy `.env.example` to `.env` and fill in your credentials:

```bash
# Core
MONGODB_URI=mongodb://localhost:27017/queenstouch
JWT_SECRET=your_super_secret_key_here

# AI (Google Gemini)
GEMINI_API_KEY=your_google_ai_studio_key
GEMINI_MODEL=gemini-2.0-flash

# Storage
GOOGLE_CLOUD_STORAGE_PROJECT_ID=your_gcp_project
GOOGLE_CLOUD_STORAGE_BUCKET=your_bucket_name
GOOGLE_CLOUD_STORAGE_CREDENTIALS_BASE64=base64_json_key
```

### 3. Running the App
```bash
./gradlew bootRun
```
The API will be available at `http://localhost:8080`.

---

## 📂 Project Architecture

```bash
src/main/java/com/queenstouch/queenstouchbackend/
├── config/           # Security, AI, GCS, and Web configurations
├── controller/       # REST endpoints (v1)
├── dto/              # Request/Response data transfer objects
├── exception/        # Global error handling logic
├── model/            # MongoDB entities (User, CvDocument, Order)
├── repository/       # Data access layer
├── service/          # Business logic & AI orchestration
│   └── ai/           # Gemini & Mock AI implementations
└── util/             # JWT, OTP, and Document Export utilities
```

---

## 📜 API Documentation

We use **OpenAPI 3** for documentation. Once the app is running, visit:

- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **JSON Spec**: `http://localhost:8080/v3/api-docs`

---

## 📧 Email Notifications
The system includes a pre-configured `MailService` using Thymeleaf templates for:
- Email Verification (OTP).
- Password Reset.
- Order Confirmations.

---

## 🧪 Testing

Run the test suite including security and integration tests:
```bash
./gradlew test
```

---

## 📝 License
This project is open-source and free for anyone to contribute to. We welcome community contributions to help improve the platform for job seekers everywhere!
