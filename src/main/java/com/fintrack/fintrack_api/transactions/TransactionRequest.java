package com.fintrack.fintrack_api.transactions;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Validated transaction data accepted by the API.
 *
 * @param amount the positive transaction amount
 * @param currency the three-letter ISO 4217 currency code
 * @param type whether the transaction is income or expense
 * @param category the transaction category
 * @param description the transaction description
 */
public record TransactionRequest(
		@NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal amount,
		@NotBlank @Pattern(regexp = "^[A-Z]{3}$") String currency,
		@NotNull TransactionType type,
		@NotNull TransactionCategory category,
		@NotBlank @Size(max = 255) String description) {
}
