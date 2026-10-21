package com.fintrack.fintrack_api.transactions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTests {

	@Mock
	private TransactionRepository transactionRepository;

	@InjectMocks
	private TransactionService transactionService;

	@Test
	void create_amountWithMoreThanTwoDecimalPlaces_roundsHalfUp() {
		TransactionRequest request = new TransactionRequest(new BigDecimal("12.345"), "USD",
				TransactionType.EXPENSE, TransactionCategory.FOOD, "Lunch");
		when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

		Transaction created = transactionService.create(7L, request);

		assertEquals(new BigDecimal("12.35"), created.getAmount());
		assertEquals(7L, created.getUserId());
	}

	@Test
	void create_amountRoundsToZero_throwsInvalidTransactionException() {
		TransactionRequest request = new TransactionRequest(new BigDecimal("0.004"), "USD",
				TransactionType.EXPENSE, TransactionCategory.FOOD, "Lunch");

		assertThrows(InvalidTransactionException.class, () -> transactionService.create(7L, request));
	}

	@Test
	void getByUser_requestedSortIsOverriddenWithCreatedAtDescending() {
		Pageable requested = PageRequest.of(2, 10, Sort.by(Sort.Direction.ASC, "amount"));
		when(transactionRepository.findAllByUserId(any(), any(Pageable.class)))
				.thenReturn(new PageImpl<>(java.util.List.of()));

		Page<Transaction> results = transactionService.getByUser(7L, requested);

		ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
		verify(transactionRepository).findAllByUserId(org.mockito.ArgumentMatchers.eq(7L), pageableCaptor.capture());
		assertEquals(2, pageableCaptor.getValue().getPageNumber());
		assertEquals(10, pageableCaptor.getValue().getPageSize());
		assertEquals(Sort.by(Sort.Direction.DESC, "createdAt"), pageableCaptor.getValue().getSort());
		assertEquals(0, results.getTotalElements());
	}

	@Test
	void getById_transactionBelongsToAnotherUser_throwsNotFound() {
		when(transactionRepository.findByIdAndUserId(12L, 7L)).thenReturn(Optional.empty());

		assertThrows(TransactionNotFoundException.class, () -> transactionService.getById(12L, 7L));
		verify(transactionRepository).findByIdAndUserId(12L, 7L);
	}

	@Test
	void deleteAllByUser_deletesOnlyCallerTransactions() {
		when(transactionRepository.deleteAllByUserId(7L)).thenReturn(2L);

		transactionService.deleteAllByUser(7L);

		verify(transactionRepository).deleteAllByUserId(7L);
	}
}
