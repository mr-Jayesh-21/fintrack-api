package com.fintrack.fintrack_api.transactions;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Applies transaction business rules and delegates data access to the repository.
 */
@Service
@Transactional(readOnly = true)
public class TransactionService {

	private static final Logger LOGGER = LoggerFactory.getLogger(TransactionService.class);
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
	 * Creates and persists a transaction for the specified user.
	 *
	 * @param userId the transaction owner's identifier
	 * @param request validated transaction data
	 * @return the persisted transaction
	 */
	@Transactional
	public Transaction create(Long userId, TransactionRequest request) {
		validateUserId(userId);
		if (request == null || request.amount() == null || request.amount().signum() <= 0) {
			throw new InvalidTransactionException("Transaction amount must be positive");
		}
		BigDecimal normalizedAmount = request.amount().setScale(2, RoundingMode.HALF_UP);
		if (normalizedAmount.signum() <= 0) {
			throw new InvalidTransactionException("Transaction amount must be positive after rounding");
		}
		if (normalizedAmount.precision() > 19) {
			throw new InvalidTransactionException("Transaction amount exceeds the supported precision");
		}

		Transaction transaction = transactionRepository.save(new Transaction(userId, normalizedAmount,
				request.currency(), request.type(), request.category(), request.description()));
		LOGGER.info("Transaction created transactionId={}", transaction.getId());
		return transaction;
	}

	/**
	 * Retrieves a page of transactions for the specified user, newest first.
	 *
	 * @param userId the transaction owner's identifier
	 * @param pageable requested page number and size
	 * @return a page containing only the user's transactions
	 */
	public Page<Transaction> getByUser(Long userId, Pageable pageable) {
		validateUserId(userId);
		if (pageable == null) {
			throw new InvalidTransactionException("Pagination information is required");
		}
		Pageable newestFirst = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
				Sort.by(Sort.Direction.DESC, "createdAt"));
		return transactionRepository.findAllByUserId(userId, newestFirst);
	}

	/**
	 * Retrieves a transaction only if it belongs to the specified user.
	 *
	 * @param transactionId the transaction identifier
	 * @param userId the transaction owner's identifier
	 * @return the owned transaction
	 * @throws TransactionNotFoundException if the transaction is missing or belongs to another user
	 */
	public Transaction getById(Long transactionId, Long userId) {
		validateUserId(userId);
		return transactionRepository.findByIdAndUserId(transactionId, userId)
				.orElseThrow(() -> new TransactionNotFoundException(transactionId));
	}

	/**
	 * Deletes a transaction only if it belongs to the specified user.
	 *
	 * @param transactionId the transaction identifier
	 * @param userId the transaction owner's identifier
	 * @throws TransactionNotFoundException if the transaction is missing or belongs to another user
	 */
	@Transactional
	public void deleteById(Long transactionId, Long userId) {
		validateUserId(userId);
		if (transactionRepository.deleteByIdAndUserId(transactionId, userId) == 0) {
			throw new TransactionNotFoundException(transactionId);
		}
		LOGGER.info("Transaction deleted transactionId={}", transactionId);
	}

	/**
	 * Deletes every transaction belonging to the specified user.
	 *
	 * @param userId the transaction owner's identifier
	 */
	@Transactional
	public void deleteAllByUser(Long userId) {
		validateUserId(userId);
		long deletedCount = transactionRepository.deleteAllByUserId(userId);
		LOGGER.warn("User transactions deleted deletedCount={}", deletedCount);
	}

	private void validateUserId(Long userId) {
		if (userId == null || userId <= 0) {
			throw new InvalidTransactionException("X-User-Id must be a positive integer");
		}
	}
}
