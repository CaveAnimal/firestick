# Testing Standards (Frontend & Backend)

Scope:
- Unit tests: JUnit 5 for Java, Vitest for UI (when npm install is available).
- Integration tests: Spring Boot Test with RANDOM_PORT; prefer hitting HTTP endpoints with TestRestTemplate or MockMvc.

Guidelines:
- At least happy path + one edge case per public API.
- Keep tests deterministic; avoid time and random without seeding.
- Name tests clearly: SubjectUnderTest_Condition_ExpectedBehavior.
- Prefer realistic test data via builders (see TestDataBuilder when available).
- Keep assertions focused; avoid over-specifying.

UI:
- Use testing-library for component behavior (post-install), and Playwright for e2e smoke flows.

Coverage:
- Target ≥80% line coverage on core services; do not reduce existing coverage.
