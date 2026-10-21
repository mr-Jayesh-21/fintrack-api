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

## 2026-10-21 17:19:12 +05:30

Act as a senior fintech backend engineer. Using #file:Transaction.java and #file:TransactionService.java as the existing pattern, design the Expense Splitting feature for a Spring Boot 3 / JPA API. Cover: the SharedExpense and ExpenseParticipant entities (creator, description, total amount, split type EQUAL/CUSTOM, participants with share amounts, created date), the rules for equal split rounding (remainder cents), custom-split validation (sum must equal total), the net balance algorithm between two users, and the two endpoints. Do not write code yet; give a short design.

## 2026-10-21 17:19:12 +05:30

Implement the Expense Splitting design in the package com.fintrack.fintrack_api.expenses following #file:.github/copilot-instructions.md. Create: SharedExpense and ExpenseParticipant entities, SplitType enum, SharedExpenseRepository, ExpenseSplitService (create expense, equal and custom split, validate custom sum == total and at least 2 participants, compute net balances per person), DTOs, controller with POST /api/expenses and GET /api/expenses/balances. Constraints: BigDecimal scale 2 HALF_UP, remainder cents go to the first participant, current user from the X-User-Id header only, 404 for expenses the user is not part of, @Valid, specific exceptions via the existing @RestControllerAdvice, SLF4J logging with no PII. Service has no persistence code except via the repository.

## 2026-10-21 17:30:17 +05:30

Write JUnit 5 + Mockito/MockMvc tests for #file:ExpenseSplitService.java, naming them method_condition_expectedResult. Example of the style I want: equalSplit_threeParticipants_sharesSumToTotal: 100.00 split 3 ways gives 33.34, 33.33, 33.33. Cover at least: equal split among 3, valid custom split, custom split with wrong sum fails, net balance across multiple expenses (A owes B 30, B owes A 10 -> A owes B 20), only 1 participant fails, and unauthorized access returns 404.