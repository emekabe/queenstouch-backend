# Pricing Catalogue — Queenstouch Global

All prices are in **Nigerian Naira (NGN)**.  
Prices are configured in `application.yml` under the `app.pricing` namespace.

## Self-Service (AI-Powered) Features

| Service Key | Label | Price |
|---|---|---|
| `STANDARD_CV` | Standard CV Builder | ₦2,500 |
| `ACADEMIC_CV` | Academic CV Builder | ₦3,000 |
| `COVER_LETTER` | Cover Letter Generator | ₦1,500 |
| `LINKEDIN_GENERATOR` | LinkedIn Profile Generator | ₦2,000 |
| `JOB_MATCH_OPTIMIZE` | CV Job Description Optimization | ₦1,000 |

## Expert (Human-Written) Services

| Service Key | Label | Price Range |
|---|---|---|
| `EXPERT_REVIEW` | Expert CV Review | ₦3,000 – ₦5,000 |
| `PRO_CV_WRITING` | Professional CV Writing | ₦10,000 – ₦25,000 |
| `PERSONAL_STATEMENT` | Personal Statement Writing | ₦8,000 – ₦20,000 |
| `MOTIVATION_LETTER` | Motivation Letter Writing | ₦8,000 – ₦20,000 |
| `LINKEDIN_OPTIMIZATION` | LinkedIn Optimization (Expert) | ₦8,000 – ₦15,000 |

## Bundles

| Service Key | Label | Bundle Price |
|---|---|---|
| `BUNDLE_JOB_SEEKER` | Job Seeker Bundle (CV + Cover Letter) | ₦3,500 |
| `BUNDLE_INTERNATIONAL_STUDY` | International Study Bundle (Academic CV + Cover Letter) | ₦4,000 |
| `BUNDLE_CAREER_UPGRADE` | Career Upgrade Bundle (CV + Cover Letter + LinkedIn) | ₦5,000 |

---

> Prices returned live via `GET /api/v1/orders/pricing`  
> Payment is currently **mocked** — every order is instantly marked PAID.
