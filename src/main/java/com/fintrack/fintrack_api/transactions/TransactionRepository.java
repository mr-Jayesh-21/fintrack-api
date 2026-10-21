package com.fintrack.fintrack_api.transactions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
/**
 * Persistence operations for transactions, scoped by owner where applicable.
 */
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
	/**
	 * Retrieves a page of transactions belonging to the specified user.
	 *
	 * @param userId the transaction owner's identifier
	 * @param pageable requested page information and ordering
	 * @return a page of the user's transactions
	 */
	Page<Transaction> findAllByUserId(Long userId, Pageable pageable);
	/**
	 * Finds a transaction only when it belongs to the specified user.
	 *
	 * @param id the transaction identifier
	 * @param userId the transaction owner's identifier
	 * @return the owned transaction, if it exists
	 */
	Optional<Transaction> findByIdAndUserId(Long id, Long userId);
	/**
	 * Deletes a transaction only when it belongs to the specified user.
	 *
	 * @param id the transaction identifier
	 * @param userId the transaction owner's identifier
	 * @return the number of deleted transactions
	 */
	long deleteByIdAndUserId(Long id, Long userId);
	/**
	 * Deletes all transactions belonging to the specified user.
	 *
	 * @param userId the transaction owner's identifier
	 * @return the number of deleted transactions
	 */
	long deleteAllByUserId(Long userId);
}
