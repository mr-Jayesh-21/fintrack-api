package com.fintrack.fintrack_api.expenses;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Participant and optional custom share supplied when creating an expense.
 *
 * @param userId the participant's identifier
 * @param shareAmount the required share for a custom split, otherwise omitted
 */
public record ExpenseParticipantRequest(
		@NotNull @Positive Long userId,
		BigDecimal shareAmount) {
}
