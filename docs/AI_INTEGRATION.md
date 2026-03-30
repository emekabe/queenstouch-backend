# AI Integration — Queenstouch Backend

## Overview

All AI features use **Spring AI** with the **Google Gemini Flash** model via the `spring-ai-google-gemini-spring-boot-starter` dependency.

The integration is abstracted behind the `AiService` interface, making it trivially swappable (e.g., to OpenAI, Anthropic, or a mock).

---

## Service Implementations

### `GeminiAiService` (default / `@Primary`)

- Wires `ChatClient.Builder` injected by Spring AI auto-configuration.
- Each method crafts a tailored prompt and calls `client.prompt().user(prompt).call().content()`.
- JSON-returning methods (score, job-match) include a JSON schema in the prompt and strip markdown code fences before parsing.

### `MockAiService` (bean name: `mockAiService`)

- Returns realistic-looking stub responses.
- Automatically falls back when `GEMINI_API_KEY` is not set (because Spring AI auto-config will fail to create the `ChatClient.Builder`, at which point you can qualify `@MockAiService` in your config).
- Always safe for local dev and CI where no API key is available.

---

## AI Features

| Feature | Method | Service |
|---|---|---|
| Professional Summary | `generateProfessionalSummary(...)` | `POST /cvs/{id}/generate-summary` |
| Achievement Bullet Builder | `generateAchievementBullet(...)` | `POST /cvs/generate-achievement` |
| Cover Letter | `generateCoverLetter(...)` | `POST /cover-letters` |
| LinkedIn Headline | `generateLinkedInHeadline(...)` | `POST /linkedin/generate` |
| LinkedIn Summary | `generateLinkedInSummary(...)` | `POST /linkedin/generate` |
| LinkedIn Skills | `generateLinkedInSkills(...)` | `POST /linkedin/generate` |
| CV Strength Score | `scoreCv(...)` | `POST /cvs/{id}/score` |
| Job Description Match | `matchJobDescription(...)` | `POST /cvs/{id}/job-match` |

---

## JSON Parsing

For endpoints that return structured data (Score, JobMatch), `CvService.parseJson()`:
1. Strips Gemini markdown code fences (` ```json ... ``` `)
2. Deserialises via Jackson `ObjectMapper` into the embedded POJO
3. Throws `AppException.internalError(...)` on parse failure

---

## Prompt Design Principles

- **Single-task prompts**: each method has one clear deliverable.
- **Output-format instructions in the prompt**: "Return ONLY the summary text. No labels, no preamble."
- **JSON schema embedded**: for structured outputs, the expected JSON schema is stated explicitly.
- **Metrics encouraged**: achievement bullets prompt for quantification.
