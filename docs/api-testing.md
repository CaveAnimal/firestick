# API Testing Patterns

- Prefer MockMvc for focused controller tests, TestRestTemplate for full-stack (port) tests.
- Assert structure first (status code, keys) before fine-grained fields.
- Seed small fixtures in H2 as needed; clean up via @Transactional when appropriate.
- For SSE endpoints, assert stream initiation and key message shapes with timeouts.
