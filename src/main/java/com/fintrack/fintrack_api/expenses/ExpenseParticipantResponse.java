package com.fintrack.fintrack_api.expenses;

import java.math.BigDecimal;

/**
 * Participant share returned by the API.
 *
 * @param userId the participant's identifier
 * @param shareAmount the participant's allocated share
 */
public record ExpenseParticipantResponse(Long userId, BigDecimal shareAmount) {

	/**
	 * Maps a participant entity to its API representation.
	 *
	 * @param participant the participant entity
	 * @return the participant response
	 */
	public static ExpenseParticipantResponse from(ExpenseParticipant participant) {
		return new ExpenseParticipantResponse(participant.getUserId(), participant.getShareAmount());
	}
}
