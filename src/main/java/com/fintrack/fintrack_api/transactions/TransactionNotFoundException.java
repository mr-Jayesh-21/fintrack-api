package com.fintrack.fintrack_api.transactions;

/**
 * Indicates that a transaction does not exist for the requesting user.
 */
public class TransactionNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * Creates a not-found exception without disclosing transaction ownership.
	 *
	 * @param transactionId the requested transaction identifier
	 */
	public TransactionNotFoundException(Long transactionId) {
		super("Transaction not found: " + transactionId);
	}
}
