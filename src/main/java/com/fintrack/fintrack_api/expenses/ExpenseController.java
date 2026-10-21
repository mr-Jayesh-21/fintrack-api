package com.fintrack.fintrack_api.expenses;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * HTTP endpoints for shared expenses and caller balances.
 */
@RestController
@RequestMapping("/api/expenses")
@Validated
public class ExpenseController {

	private final ExpenseSplitService expenseSplitService;

	/**
	 * Creates the controller with its expense service.
	 *
	 * @param expenseSplitService service handling expense operations
	 */
	public ExpenseController(ExpenseSplitService expenseSplitService) {
		this.expenseSplitService = expenseSplitService;
	}

	/**
	 * Creates a shared expense for the caller.
	 *
	 * @param userId the caller's identifier from {@code X-User-Id}
	 * @param request validated expense data
	 * @return the created expense response
	 */
	@PostMapping
	public ResponseEntity<SharedExpenseResponse> create(
			@RequestHeader("X-User-Id") Long userId,
			@Valid @RequestBody SharedExpenseRequest request) {
		SharedExpense created = expenseSplitService.create(userId, request);
		return ResponseEntity.status(HttpStatus.CREATED).body(SharedExpenseResponse.from(created));
	}

	/**
	 * Retrieves the caller's net balances, optionally scoped to one participated expense.
	 *
	 * @param userId the caller's identifier from {@code X-User-Id}
	 * @param expenseId optional expense identifier
	 * @return balances grouped by counterparty and currency
	 */
	@GetMapping("/balances")
	public List<ExpenseBalanceResponse> getBalances(
			@RequestHeader("X-User-Id") Long userId,
			@RequestParam(required = false) @Positive Long expenseId) {
		return expenseSplitService.getBalances(userId, expenseId);
	}
}
