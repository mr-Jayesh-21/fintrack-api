package com.fintrack.fintrack_api.transactions;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * HTTP endpoints for user-owned transactions.
 */
@RestController
@RequestMapping("/api/transactions")
@Validated
public class TransactionController {

	private final TransactionService transactionService;

	/**
	 * Creates the controller with its transaction service.
	 *
	 * @param transactionService service handling transaction operations
	 */
	public TransactionController(TransactionService transactionService) {
		this.transactionService = transactionService;
	}

	/**
	 * Creates a transaction owned by the caller identified in the request header.
	 *
	 * @param userId the caller's identifier from {@code X-User-Id}
	 * @param request validated transaction data
	 * @return the created transaction response
	 */
	@PostMapping
	public ResponseEntity<TransactionResponse> create(
			@RequestHeader("X-User-Id") Long userId,
			@Valid @RequestBody TransactionRequest request) {
		Transaction created = transactionService.create(userId, request);
		return ResponseEntity.status(HttpStatus.CREATED).body(TransactionResponse.from(created));
	}

	/**
	 * Retrieves a page of the caller's transactions, ordered newest first.
	 *
	 * @param userId the caller's identifier from {@code X-User-Id}
	 * @param page zero-based page number
	 * @param size requested page size, limited to 100
	 * @return a page of transaction response objects
	 */
	@GetMapping
	public Page<TransactionResponse> getByUser(
			@RequestHeader("X-User-Id") Long userId,
			@RequestParam(defaultValue = "0") @Min(0) int page,
			@RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
		return transactionService.getByUser(userId, PageRequest.of(page, size))
				.map(TransactionResponse::from);
	}

	/**
	 * Retrieves a transaction only when it belongs to the caller.
	 *
	 * @param userId the caller's identifier from {@code X-User-Id}
	 * @param transactionId the transaction identifier
	 * @return the transaction response
	 */
	@GetMapping("/{transactionId}")
	public TransactionResponse getById(
			@RequestHeader("X-User-Id") Long userId,
			@PathVariable Long transactionId) {
		return TransactionResponse.from(transactionService.getById(transactionId, userId));
	}

	/**
	 * Deletes a transaction only when it belongs to the caller.
	 *
	 * @param userId the caller's identifier from {@code X-User-Id}
	 * @param transactionId the transaction identifier
	 * @return an empty response with status 204
	 */
	@DeleteMapping("/{transactionId}")
	public ResponseEntity<Void> deleteById(
			@RequestHeader("X-User-Id") Long userId,
			@PathVariable Long transactionId) {
		transactionService.deleteById(transactionId, userId);
		return ResponseEntity.noContent().build();
	}

	/**
	 * Deletes all transactions belonging to the caller.
	 *
	 * @param userId the caller's identifier from {@code X-User-Id}
	 * @return an empty response with status 204
	 */
	@DeleteMapping
	public ResponseEntity<Void> deleteAllByUser(@RequestHeader("X-User-Id") Long userId) {
		transactionService.deleteAllByUser(userId);
		return ResponseEntity.noContent().build();
	}
}
