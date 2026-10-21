package com.fintrack.fintrack_api.transactions;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Transaction data returned by the API without exposing the persistence entity.
 *
 * @param id the transaction identifier
 * @param amount the transaction amount
 * @param currency the ISO 4217 currency code
 * @param type whether the transaction is income or expense
 * @param category the transaction category
 * @param description the transaction description
 * @param createdAt the transaction creation timestamp
 * @param version the optimistic-lock version
 */
public record TransactionResponse(
		Long id,
		BigDecimal amount,
		String currency,
		TransactionType type,
		TransactionCategory category,
		String description,
		Instant createdAt,
		Long version) {

	/**
	 * Maps a persisted transaction to its API response representation.
	 *
	 * @param transaction the transaction to map
	 * @return the transaction response
	 */
	public static TransactionResponse from(Transaction transaction) {
		return new TransactionResponse(transaction.getId(), transaction.getAmount(), transaction.getCurrency(),
				transaction.getType(), transaction.getCategory(), transaction.getDescription(),
				transaction.getCreatedAt(), transaction.getVersion());
	}
}
