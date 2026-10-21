## 2026-10-21 15:41:30 +05:30

Write the full content of #file:.github/copilot-instructions.md for a Spring Boot 3 / Java 17 fintech API using Spring Data JPA. Use these sections:

1. Tech Stack: Java 17, Spring Boot 3.x, Spring Data JPA (Hibernate), H2, JUnit 5, Mockito, MockMvc, Maven.
2. Architecture: Controller -> Service -> Repository -> Entity. DTOs at the API boundary (never expose entities). No database code in services. Base package is com.fintrack.fintrack_api; all code goes in sub-packages like com.fintrack.fintrack_api.transactions and com.fintrack.fintrack_api.expenses.
3. Coding Standards: PascalCase classes, camelCase methods, constructor injection, BigDecimal for all money (never float or double), SLF4J logging, Javadoc on all public methods.
4. Security: no hardcoded secrets, validate all input with @Valid, users can only access their own data, never log PII or full amounts with user identifiers, throw specific exceptions (not generic Exception), a global @RestControllerAdvice for error handling.
5. Testing: JUnit 5 + Mockito, cover happy path, edge cases and error scenarios, name tests method_condition_expectedResult.
6. Prompt Logging: Append every prompt I send to PROMPTS.md with a timestamp.

Use bullet points. Be specific, not vague.

## 2026-10-21 16:03:10 +05:30

Generate a Transaction model and a Transaction service with create, get-by-user, and delete-all functions. Use a database.

## 2026-10-21 17:05:08 +05:30

In #file:TransactionService.java, make sure the service contains no persistence-specific code or SQL, uses the repository only, normalises amount with setScale(2, RoundingMode.HALF_UP) before saving, and logs create and delete with SLF4J. Show minimal changes only.