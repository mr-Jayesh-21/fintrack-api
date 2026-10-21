package com.fintrack.fintrack_api.transactions;

import java.time.Instant;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.fintrack.fintrack_api.expenses.ExpenseNotFoundException;
import com.fintrack.fintrack_api.expenses.InvalidExpenseException;

/**
 * Produces consistent, safe error responses for transaction API failures.
 */
@RestControllerAdvice
public class TransactionExceptionHandler {

	/**
	 * Converts missing or non-owned transaction failures to a not-found response.
	 *
	 * @param exception the transaction lookup failure
	 * @return a safe error response
	 */
	@ExceptionHandler(TransactionNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ApiError handleNotFound(TransactionNotFoundException exception) {
		return new ApiError(Instant.now(), HttpStatus.NOT_FOUND.value(), "TRANSACTION_NOT_FOUND",
				"Transaction not found");
	}

	/**
	 * Converts invalid transaction data to a bad-request response.
	 *
	 * @param exception the invalid transaction failure
	 * @return a safe error response
	 */
	@ExceptionHandler(InvalidTransactionException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiError handleInvalidTransaction(InvalidTransactionException exception) {
		return new ApiError(Instant.now(), HttpStatus.BAD_REQUEST.value(), "INVALID_TRANSACTION",
				exception.getMessage());
	}

	/**
	 * Converts missing or non-participant expense failures to a not-found response.
	 *
	 * @param exception the expense lookup failure
	 * @return a safe error response
	 */
	@ExceptionHandler(ExpenseNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ApiError handleExpenseNotFound(ExpenseNotFoundException exception) {
		return new ApiError(Instant.now(), HttpStatus.NOT_FOUND.value(), "EXPENSE_NOT_FOUND",
				"Expense not found");
	}

	/**
	 * Converts invalid expense data to a bad-request response.
	 *
	 * @param exception the invalid expense failure
	 * @return a safe error response
	 */
	@ExceptionHandler(InvalidExpenseException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiError handleInvalidExpense(InvalidExpenseException exception) {
		return new ApiError(Instant.now(), HttpStatus.BAD_REQUEST.value(), "INVALID_EXPENSE",
				exception.getMessage());
	}

	/**
	 * Converts bean validation failures to a consistent bad-request response.
	 *
	 * @param exception the validation failure
	 * @return a safe error response
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiError handleValidation(MethodArgumentNotValidException exception) {
		return new ApiError(Instant.now(), HttpStatus.BAD_REQUEST.value(), "VALIDATION_FAILED",
				"Request validation failed");
	}

	/**
	 * Converts method parameter validation failures to a consistent bad-request response.
	 *
	 * @param exception the constraint violation failure
	 * @return a safe error response
	 */
	@ExceptionHandler(ConstraintViolationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiError handleConstraintViolation(ConstraintViolationException exception) {
		return new ApiError(Instant.now(), HttpStatus.BAD_REQUEST.value(), "VALIDATION_FAILED",
				"Request validation failed");
	}

	/**
	 * Converts malformed request bodies to a consistent bad-request response.
	 *
	 * @param exception the unreadable request failure
	 * @return a safe error response
	 */
	@ExceptionHandler(HttpMessageNotReadableException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiError handleUnreadableRequest(HttpMessageNotReadableException exception) {
		return new ApiError(Instant.now(), HttpStatus.BAD_REQUEST.value(), "INVALID_REQUEST",
				"Request body is invalid");
	}

	/**
	 * Converts missing caller identity headers to a consistent bad-request response.
	 *
	 * @param exception the missing-header failure
	 * @return a safe error response
	 */
	@ExceptionHandler(MissingRequestHeaderException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiError handleMissingHeader(MissingRequestHeaderException exception) {
		return new ApiError(Instant.now(), HttpStatus.BAD_REQUEST.value(), "INVALID_REQUEST",
				"X-User-Id header is required");
	}

	/**
	 * Converts invalid numeric header or path parameters to a consistent bad-request response.
	 *
	 * @param exception the parameter conversion failure
	 * @return a safe error response
	 */
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiError handleTypeMismatch(MethodArgumentTypeMismatchException exception) {
		return new ApiError(Instant.now(), HttpStatus.BAD_REQUEST.value(), "INVALID_REQUEST",
				"Request parameter is invalid");
	}

	/**
	 * API error response representation.
	 *
	 * @param timestamp when the error was generated
	 * @param status the HTTP status code
	 * @param code stable machine-readable error code
	 * @param message safe human-readable explanation
	 */
	public record ApiError(Instant timestamp, int status, String code, String message) {
	}
}
