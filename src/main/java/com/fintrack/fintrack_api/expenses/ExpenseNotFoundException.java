package com.fintrack.fintrack_api.expenses;

/**
 * Indicates that an expense is missing or the caller is not a participant.
 */
public class ExpenseNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * Creates a not-found exception without disclosing expense ownership.
	 *
	 * @param expenseId the requested expense identifier
	 */
	public ExpenseNotFoundException(Long expenseId) {
		super("Expense not found: " + expenseId);
	}
}
