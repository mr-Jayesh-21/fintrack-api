package com.fintrack.fintrack_api.expenses;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import com.fintrack.fintrack_api.transactions.TransactionExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class ExpenseControllerTests {

	@Mock
	private SharedExpenseRepository sharedExpenseRepository;

	private MockMvc mockMvc;

	/**
	 * Creates a standalone MVC test with the real service and global error advice.
	 */
	@BeforeEach
	void setUp() {
		ExpenseSplitService expenseSplitService = new ExpenseSplitService(sharedExpenseRepository);
		ExpenseController expenseController = new ExpenseController(expenseSplitService);
		mockMvc = MockMvcBuilders.standaloneSetup(expenseController)
				.setControllerAdvice(new TransactionExceptionHandler())
				.build();
	}

	@Test
	void getBalances_expenseBelongsToAnotherUser_returnsNotFound() throws Exception {
		when(sharedExpenseRepository.findDistinctByIdAndParticipants_UserId(42L, 7L))
				.thenReturn(Optional.empty());

		mockMvc.perform(get("/api/expenses/balances")
						.header("X-User-Id", 7L)
						.param("expenseId", "42"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.code").value("EXPENSE_NOT_FOUND"))
				.andExpect(jsonPath("$.message").value("Expense not found"));

		verify(sharedExpenseRepository).findDistinctByIdAndParticipants_UserId(42L, 7L);
	}
}
