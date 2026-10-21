package com.fintrack.fintrack_api.expenses;

import java.math.BigDecimal;

/**
 * Net balance with another user in one currency.
 *
 * @param otherUserId the other user's identifier
 * @param currency the currency code
 * @param amount the nonnegative net amount
 * @param direction the direction relative to the caller
 */
public record ExpenseBalanceResponse(
		Long otherUserId,
		String currency,
		BigDecimal amount,
		BalanceDirection direction) {
}
