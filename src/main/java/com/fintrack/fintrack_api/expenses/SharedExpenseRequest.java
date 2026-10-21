package com.fintrack.fintrack_api.expenses;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Validated data accepted when creating a shared expense.
 *
 * @param description the expense description
 * @param totalAmount the positive total amount
 * @param currency the three-letter ISO 4217 currency code
 * @param splitType the share allocation rule
 * @param participants the participants, including the creator
 */
public record SharedExpenseRequest(
		@NotBlank @Size(max = 255) String description,
		@NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal totalAmount,
		@NotBlank @Pattern(regexp = "^[A-Z]{3}$") String currency,
		@NotNull SplitType splitType,
		@NotNull @Size(min = 2) List<@Valid ExpenseParticipantRequest> participants) {
}
