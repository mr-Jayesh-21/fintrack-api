# fintrack-api

fintrack-api is a fintech personal finance API for managing expenses, including an Expense Splitting feature to help people share costs.

## Tech Stack

- Java 17 and Spring Boot 3.x
- Spring Data JPA (Hibernate) with H2 for development
- JUnit 5, Mockito, and MockMvc
- Maven

## Project Structure

- `src/main/java/com/fintrack/fintrack_api/` — application code, organized into feature sub-packages
- `src/main/resources/` — application configuration and web resources
- `src/test/java/` — automated tests

Transaction module lives in `com.fintrack.fintrack_api.transactions` (Maven equivalent of `src/transactions/`).

## Run

Start the application:

```cmd
mvnw.cmd spring-boot:run
```

Run tests:

```cmd
mvnw.cmd test
```