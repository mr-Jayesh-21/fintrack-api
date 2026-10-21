package com.fintrack.fintrack_api.expenses;

/**
 * Indicates invalid expense data or an invalid caller identifier.
 */
public class InvalidExpenseException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * Creates an exception for invalid expense input.
	 *
	 * @param message a safe explanation of the validation failure
	 */
	public InvalidExpenseException(String message) {
		super(message);
	}
}
