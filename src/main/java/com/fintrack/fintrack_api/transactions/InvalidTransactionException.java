package com.fintrack.fintrack_api.transactions;

/**
 * Indicates that transaction data or the requesting user identifier is invalid.
 */
public class InvalidTransactionException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * Creates an exception for invalid transaction input.
	 *
	 * @param message a safe explanation of the validation failure
	 */
	public InvalidTransactionException(String message) {
		super(message);
	}
}
