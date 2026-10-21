package com.fintrack.fintrack_api.expenses;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExpenseSplitServiceTests {

	@Mock
	private SharedExpenseRepository sharedExpenseRepository;

	@InjectMocks
	private ExpenseSplitService expenseSplitService;

	@Test
	void equalSplit_threeParticipants_sharesSumToTotal() {
		SharedExpenseRequest request = new SharedExpenseRequest("Dinner", new BigDecimal("100.00"), "USD",
				SplitType.EQUAL, List.of(new ExpenseParticipantRequest(7L, null),
						new ExpenseParticipantRequest(8L, null), new ExpenseParticipantRequest(9L, null)));
		when(sharedExpenseRepository.save(any(SharedExpense.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));

		SharedExpense created = expenseSplitService.create(7L, request);

		assertEquals(new BigDecimal("33.34"), created.getParticipants().get(0).getShareAmount());
		assertEquals(new BigDecimal("33.33"), created.getParticipants().get(1).getShareAmount());
		assertEquals(new BigDecimal("33.33"), created.getParticipants().get(2).getShareAmount());
		assertEquals(new BigDecimal("100.00"), created.getParticipants().stream()
				.map(ExpenseParticipant::getShareAmount)
				.reduce(BigDecimal.ZERO, BigDecimal::add));
	}

	@Test
	void customSplit_validShares_persistsMatchingShares() {
		SharedExpenseRequest request = new SharedExpenseRequest("Dinner", new BigDecimal("10.00"), "USD",
				SplitType.CUSTOM, List.of(new ExpenseParticipantRequest(7L, new BigDecimal("4.25")),
						new ExpenseParticipantRequest(8L, new BigDecimal("5.75"))));
		when(sharedExpenseRepository.save(any(SharedExpense.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));

		SharedExpense created = expenseSplitService.create(7L, request);

		assertEquals(new BigDecimal("4.25"), created.getParticipants().get(0).getShareAmount());
		assertEquals(new BigDecimal("5.75"), created.getParticipants().get(1).getShareAmount());
		verify(sharedExpenseRepository).save(any(SharedExpense.class));
	}

	@Test
	void customSplit_wrongSum_throwsInvalidExpenseException() {
		SharedExpenseRequest request = new SharedExpenseRequest("Dinner", new BigDecimal("10.00"), "USD",
				SplitType.CUSTOM, List.of(new ExpenseParticipantRequest(7L, new BigDecimal("4.00")),
						new ExpenseParticipantRequest(8L, new BigDecimal("5.00"))));

		assertThrows(InvalidExpenseException.class, () -> expenseSplitService.create(7L, request));
	}

	@Test
	void create_oneParticipant_throwsInvalidExpenseException() {
		SharedExpenseRequest request = new SharedExpenseRequest("Coffee", new BigDecimal("5.00"), "USD",
				SplitType.EQUAL, List.of(new ExpenseParticipantRequest(7L, null)));

		assertThrows(InvalidExpenseException.class, () -> expenseSplitService.create(7L, request));
	}

	@Test
	void getBalances_expenseDoesNotIncludeCaller_throwsNotFound() {
		when(sharedExpenseRepository.findDistinctByIdAndParticipants_UserId(12L, 7L))
				.thenReturn(Optional.empty());

		assertThrows(ExpenseNotFoundException.class, () -> expenseSplitService.getBalances(7L, 12L));
		verify(sharedExpenseRepository).findDistinctByIdAndParticipants_UserId(12L, 7L);
	}

	@Test
	void getBalances_callerCreatedExpense_reportsWhatOthersOwe() {
		SharedExpense expense = new SharedExpense(7L, "Dinner", new BigDecimal("20.00"), "USD", SplitType.EQUAL);
		expense.addParticipant(new ExpenseParticipant(7L, new BigDecimal("10.00")));
		expense.addParticipant(new ExpenseParticipant(8L, new BigDecimal("10.00")));
		when(sharedExpenseRepository.findDistinctByParticipants_UserIdOrderByCreatedAtAsc(7L))
				.thenReturn(List.of(expense));

		List<ExpenseBalanceResponse> balances = expenseSplitService.getBalances(7L, null);

		assertEquals(1, balances.size());
		assertEquals(8L, balances.get(0).otherUserId());
		assertEquals(new BigDecimal("10.00"), balances.get(0).amount());
		assertEquals(BalanceDirection.THEY_OWE_CALLER, balances.get(0).direction());
	}

	@Test
	void getBalances_twoUsersOweAcrossMultipleExpenses_netsToTwenty() {
		SharedExpense aOwesB = new SharedExpense(8L, "Dinner", new BigDecimal("60.00"), "USD", SplitType.CUSTOM);
		aOwesB.addParticipant(new ExpenseParticipant(8L, new BigDecimal("30.00")));
		aOwesB.addParticipant(new ExpenseParticipant(7L, new BigDecimal("30.00")));
		SharedExpense bOwesA = new SharedExpense(7L, "Taxi", new BigDecimal("20.00"), "USD", SplitType.CUSTOM);
		bOwesA.addParticipant(new ExpenseParticipant(7L, new BigDecimal("10.00")));
		bOwesA.addParticipant(new ExpenseParticipant(8L, new BigDecimal("10.00")));
		when(sharedExpenseRepository.findDistinctByParticipants_UserIdOrderByCreatedAtAsc(7L))
				.thenReturn(List.of(aOwesB, bOwesA));

		List<ExpenseBalanceResponse> balances = expenseSplitService.getBalances(7L, null);

		assertEquals(1, balances.size());
		assertEquals(8L, balances.get(0).otherUserId());
		assertEquals(new BigDecimal("20.00"), balances.get(0).amount());
		assertEquals(BalanceDirection.CALLER_OWES, balances.get(0).direction());
	}
}
