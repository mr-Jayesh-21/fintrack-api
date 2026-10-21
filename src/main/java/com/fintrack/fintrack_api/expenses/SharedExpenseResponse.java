package com.fintrack.fintrack_api.expenses;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Shared expense API representation, excluding persistence details.
 *
 * @param id the expense identifier
 * @param creatorUserId the creator's identifier
 * @param description the expense description
 * @param totalAmount the total amount
 * @param currency the expense currency
 * @param splitType the allocation rule
 * @param participants the participant shares
 * @param createdAt the creation timestamp
 */
public record SharedExpenseResponse(
		Long id,
		Long creatorUserId,
		String description,
		BigDecimal totalAmount,
		String currency,
		SplitType splitType,
		List<ExpenseParticipantResponse> participants,
		Instant createdAt) {

	/**
	 * Maps a shared expense entity to its API representation.
	 *
	 * @param expense the shared expense entity
	 * @return the expense response
	 */
	public static SharedExpenseResponse from(SharedExpense expense) {
		List<ExpenseParticipantResponse> participantResponses = expense.getParticipants().stream()
				.map(ExpenseParticipantResponse::from)
				.toList();
		return new SharedExpenseResponse(expense.getId(), expense.getCreatorUserId(), expense.getDescription(),
				expense.getTotalAmount(), expense.getCurrency(), expense.getSplitType(), participantResponses,
				expense.getCreatedAt());
	}
}
