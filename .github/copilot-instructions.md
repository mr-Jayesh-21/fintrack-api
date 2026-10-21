# Copilot Instructions

## 1. Tech Stack

- Use Java 17 and Spring Boot 3.x.
- Use Spring Data JPA with Hibernate for persistence and H2 as the database.
- Use Maven to build and manage the project.
- Use JUnit 5, Mockito, and MockMvc for automated testing.

## 2. Architecture

- Keep application flow in this order: **Controller -> Service -> Repository -> Entity**.
- Controllers handle HTTP concerns, validate and translate API requests, and return response DTOs.
- Services contain business rules and coordinate operations; they must not contain database or persistence code.
- Repositories are the persistence boundary and use Spring Data JPA to access entities.
- Use DTOs at the API boundary. Never expose JPA entities in request or response bodies; map between DTOs and entities in the appropriate application layer.
- The base package is `com.fintrack.fintrack_api`. Put all code in feature sub-packages, such as `com.fintrack.fintrack_api.transactions` and `com.fintrack.fintrack_api.expenses`.

## 3. Coding Standards

- Name classes and other types in `PascalCase`; name methods and variables in `camelCase`.
- Use constructor injection for dependencies; do not use field injection.
- Represent all monetary values with `BigDecimal`. Never use `float` or `double` for money.
- Use SLF4J for application logging.
- Add Javadoc to every public method.

## 4. Security

- Never hardcode secrets, credentials, or keys; use configuration or environment-provided values.
- Validate incoming request data with Jakarta Bean Validation and `@Valid`.
- Enforce ownership checks on every operation involving user data: users may access only their own records.
- Never log personally identifiable information (PII), or log full monetary amounts together with user identifiers.
- Throw specific, meaningful exception types rather than generic `Exception`.
- Handle API errors consistently through a global `@RestControllerAdvice`; return safe error responses that do not expose internal details.

## 5. Testing

- Write tests with JUnit 5 and Mockito; use MockMvc for HTTP/controller behavior.
- Cover the happy path, relevant edge cases, and error scenarios for each behavior.
- Name tests using `method_condition_expectedResult`, for example `splitExpense_invalidParticipant_returnsBadRequest`.

## 6. Prompt Logging

- Append every user prompt to the repository's `PROMPTS.md` file.
- Include a timestamp with each prompt, using the format `YYYY-MM-DD HH:mm:ss z`.
- Preserve existing entries and append new entries; do not overwrite the prompt log.
