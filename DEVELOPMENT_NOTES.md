# Loan Application Service — Development Notes

## Overall approach

The service follows a **layered architecture**: REST controllers delegate to an application service that orchestrates **pure financial helpers** (EMI, risk band, interest rate) and **JPA persistence** for audit. Request validation uses Jakarta Bean Validation on DTO records so invalid payloads return **HTTP 400** before business logic runs.

Eligibility is evaluated in a **fixed order**: credit score and age/tenure checks run first without computing pricing; only when those pass do we classify risk, derive the **final annual rate**, compute **EMI**, and apply the **60%** and **50%** income thresholds. This avoids assigning a risk band or rate when the application is already invalid on credit or age rules.

## Key design decisions

1. **Single offer at requested tenure** — No alternate tenures are generated; the offer always mirrors `loan.tenureMonths`.
2. **EMI uses the final priced rate** — The same rate used for the offer (base 12% plus premiums) drives EMI and the 50%/60% checks, matching the pricing model end-to-end.
3. **Rejection reasons are explicit enums** — Mapped to stable string codes in API responses for clients and audits.
4. **H2 in-memory database** — Keeps local runs simple; swap the datasource for PostgreSQL or similar in production with no code changes beyond configuration.
5. **BigDecimal everywhere for money** — EMI and thresholds use `BigDecimal` with `HALF_UP` scale 2 for currency amounts, with a higher precision intermediate path inside `EmiCalculator`.

## Trade-offs

| Choice | Benefit | Cost |
|--------|---------|------|
| H2 in-memory | Zero setup, fast tests | Not suitable for multi-instance production |
| Element collection for rejection reasons | Simple schema | Extra table; large reason lists are rare |
| Synchronous processing only | Simple model | No queue for peak load |

## Assumptions

- **Indian numbering for “50,00,000”** is interpreted as **5,000,000** in the loan amount cap.
- **Age + tenure** uses `tenureMonths / 12` with sufficient decimal precision; boundary **65** is exclusive on the high side (reject when **greater than** 65, not equal).
- When **EMI is greater than 60%** of income, only `EMI_EXCEEDS_60_PERCENT` is recorded (not also the 50% code), since the 60% rule already implies failure of the stricter offer rule.

## Improvements with more time

- **OpenAPI** document and contract tests for the POST `/applications` API.
- **Idempotency** keys for duplicate submission protection.
- **PostgreSQL** with Flyway migrations and indexes on `created_at` / `status` for reporting.
- **Async audit** or append-only event log for compliance.
- **Configurable** base rate and premium tables via database or config service.
- **Stricter nested validation messages** (full JSON paths like `applicant.age`) in 400 responses.

## Refactoring evidence

The EMI formula, risk classification, and rate stacking live in **small, testable components** (`EmiCalculator`, `RiskBandClassifier`, `InterestRateCalculator`) rather than inside the service class, so unit tests can target formulas without Spring or databases. The application service remains a thin orchestration layer that could later be split if workflows grow (e.g. separate “pricing” and “eligibility” services).
