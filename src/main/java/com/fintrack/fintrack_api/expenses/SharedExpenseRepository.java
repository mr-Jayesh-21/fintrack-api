package com.fintrack.fintrack_api.expenses;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Persistence boundary for shared expenses and caller participation queries.
 */
public interface SharedExpenseRepository extends JpaRepository<SharedExpense, Long> {

	/**
	 * Retrieves expenses in which the user participates, oldest first.
	 *
	 * @param userId the participant's identifier
	 * @return only expenses involving the specified user
	 */
	List<SharedExpense> findDistinctByParticipants_UserIdOrderByCreatedAtAsc(Long userId);

	/**
	 * Finds an expense only when the specified user is one of its participants.
	 *
	 * @param id the expense identifier
	 * @param userId the participant's identifier
	 * @return the expense if the user participates in it
	 */
	Optional<SharedExpense> findDistinctByIdAndParticipants_UserId(Long id, Long userId);
}
