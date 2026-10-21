package com.fintrack.fintrack_api.transactions;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

	/**
	 * Retrieves all transactions owned by the given user.
	 *
	 * @param userId the transaction owner's identifier
	 * @return the user's transactions
	 */
	List<Transaction> findAllByUserId(Long userId);
}
