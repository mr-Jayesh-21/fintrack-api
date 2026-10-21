package com.fintrack.fintrack_api.transactions;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class TransactionService {
	private final TransactionRepository transactionRepository;
	/**
	 * Creates the service with its persistence dependency.
	 *
	 * @param transactionRepository repository used to persist and query transactions
	 */
	public TransactionService(TransactionRepository transactionRepository) {
		this.transactionRepository = transactionRepository;
	}
	/**
	 * Creates and persists a transaction for the given user.
	 *
	 * @param userId the transaction owner's identifier
	 * @param amount the transaction amount
	 * @param description the transaction description
	 * @param category the transaction category
	 * @return the persisted transaction
	 */
	@Transactional
	public Transaction create(Long userId, BigDecimal amount, String description, String category) {
		return transactionRepository.save(new Transaction(userId, amount, description, category));
	}
	/**
	 * Retrieves all transactions belonging to the given user.
	 *
	 * @param userId the transaction owner's identifier
	 * @return the user's transactions
	 */
	public List<Transaction> getByUser(Long userId) {
		return transactionRepository.findAllByUserId(userId);
	}
	/**
	 * Deletes every transaction from the database.
	 */
	@Transactional
	public void deleteAll() {
		transactionRepository.deleteAllInBatch();
	}
}
