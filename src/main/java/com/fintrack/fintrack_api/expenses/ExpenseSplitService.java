package com.fintrack.fintrack_api.expenses;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Applies expense-splitting rules and delegates persistence to its repository.
 */
@Service
@Transactional(readOnly = true)
public class ExpenseSplitService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExpenseSplitService.class);
	private static final int MONEY_SCALE = 2;
	private final SharedExpenseRepository sharedExpenseRepository;

	/**
	 * Creates the service with its persistence dependency.
	 *
	 * @param sharedExpenseRepository repository used to persist and query expenses
	 */
	public ExpenseSplitService(SharedExpenseRepository sharedExpenseRepository) {
		this.sharedExpenseRepository = sharedExpenseRepository;
	}

	/**
	 * Creates an expense and calculates participant shares according to its split type.
	 *
	 * @param creatorUserId the caller's identifier from {@code X-User-Id}
	 * @param request validated expense data
	 * @return the persisted expense
	 */
	@Transactional
	public SharedExpense create(Long creatorUserId, SharedExpenseRequest request) {
		validateUserId(creatorUserId);
		if (request == null || request.totalAmount() == null || request.splitType() == null
				|| request.participants() == null || request.participants().size() < 2) {
			throw new InvalidExpenseException("An expense requires a total, split type, and at least two participants");
		}

		BigDecimal total = normalizePositiveAmount(request.totalAmount(), "Expense total");
		validateParticipants(creatorUserId, request.participants());
		List<BigDecimal> shares = request.splitType() == SplitType.EQUAL
				? equalShares(total, request.participants().size())
				: customShares(total, request.participants());

		SharedExpense expense = new SharedExpense(creatorUserId, request.description(), total, request.currency(),
				request.splitType());
		for (int index = 0; index < request.participants().size(); index++) {
			expense.addParticipant(new ExpenseParticipant(request.participants().get(index).userId(), shares.get(index)));
		}
		SharedExpense saved = sharedExpenseRepository.save(expense);
		LOGGER.info("Shared expense created expenseId={}", saved.getId());
		return saved;
	}

	/**
	 * Computes balances per counterparty and currency, restricted to caller-participated expenses.
	 *
	 * @param userId the caller's identifier from {@code X-User-Id}
	 * @param expenseId optional expense to scope the result to
	 * @return the caller's net balances
	 */
	public List<ExpenseBalanceResponse> getBalances(Long userId, Long expenseId) {
		validateUserId(userId);
		List<SharedExpense> expenses;
		if (expenseId == null) {
			expenses = sharedExpenseRepository.findDistinctByParticipants_UserIdOrderByCreatedAtAsc(userId);
		} else {
			if (expenseId <= 0) {
				throw new InvalidExpenseException("Expense ID must be a positive integer");
			}
			SharedExpense expense = sharedExpenseRepository.findDistinctByIdAndParticipants_UserId(expenseId, userId)
					.orElseThrow(() -> new ExpenseNotFoundException(expenseId));
			expenses = List.of(expense);
		}

		Map<BalanceKey, BigDecimal> netByCounterpartyAndCurrency = new HashMap<>();
		for (SharedExpense expense : expenses) {
			if (expense.getCreatorUserId().equals(userId)) {
				for (ExpenseParticipant participant : expense.getParticipants()) {
					if (participant.getUserId().equals(userId)) {
						continue;
					}
					BalanceKey key = new BalanceKey(participant.getUserId(), expense.getCurrency());
					netByCounterpartyAndCurrency.merge(key, participant.getShareAmount().negate(), BigDecimal::add);
				}
			} else {
				ExpenseParticipant callerShare = expense.getParticipants().stream()
						.filter(participant -> participant.getUserId().equals(userId))
						.findFirst()
						.orElseThrow(() -> new ExpenseNotFoundException(expense.getId()));
				BalanceKey key = new BalanceKey(expense.getCreatorUserId(), expense.getCurrency());
				netByCounterpartyAndCurrency.merge(key, callerShare.getShareAmount(), BigDecimal::add);
			}
		}

		return netByCounterpartyAndCurrency.entrySet().stream()
				.sorted(Map.Entry.comparingByKey(Comparator.comparing(BalanceKey::otherUserId)
						.thenComparing(BalanceKey::currency)))
				.map(entry -> toBalanceResponse(entry.getKey(), entry.getValue()))
				.toList();
	}

	private List<BigDecimal> equalShares(BigDecimal total, int participantCount) {
		BigInteger totalCents = total.movePointRight(MONEY_SCALE).toBigIntegerExact();
		BigInteger[] division = totalCents.divideAndRemainder(BigInteger.valueOf(participantCount));
		List<BigDecimal> shares = new ArrayList<>(participantCount);
		for (int index = 0; index < participantCount; index++) {
			BigInteger cents = division[0];
			if (index == 0) {
				cents = cents.add(division[1]);
			}
			shares.add(new BigDecimal(cents, MONEY_SCALE));
		}
		return shares;
	}

	private List<BigDecimal> customShares(BigDecimal total, List<ExpenseParticipantRequest> participants) {
		List<BigDecimal> shares = new ArrayList<>(participants.size());
		BigDecimal sum = BigDecimal.ZERO.setScale(MONEY_SCALE);
		for (ExpenseParticipantRequest participant : participants) {
			if (participant.shareAmount() == null || participant.shareAmount().signum() <= 0) {
				throw new InvalidExpenseException("Every custom participant share must be positive");
			}
			BigDecimal share = participant.shareAmount().setScale(MONEY_SCALE, RoundingMode.HALF_UP);
			if (share.signum() <= 0 || share.precision() > 19) {
				throw new InvalidExpenseException("Custom participant shares must be positive supported amounts");
			}
			shares.add(share);
			sum = sum.add(share);
		}
		if (sum.compareTo(total) != 0) {
			throw new InvalidExpenseException("Custom participant shares must sum to the expense total");
		}
		return shares;
	}

	private BigDecimal normalizePositiveAmount(BigDecimal amount, String label) {
		if (amount.signum() <= 0) {
			throw new InvalidExpenseException(label + " must be positive");
		}
		BigDecimal normalized = amount.setScale(MONEY_SCALE, RoundingMode.HALF_UP);
		if (normalized.signum() <= 0) {
			throw new InvalidExpenseException(label + " must be positive after rounding");
		}
		if (normalized.precision() > 19) {
			throw new InvalidExpenseException(label + " exceeds the supported precision");
		}
		return normalized;
	}

	private void validateParticipants(Long creatorUserId, List<ExpenseParticipantRequest> participants) {
		Set<Long> userIds = new HashSet<>();
		boolean creatorIncluded = false;
		for (ExpenseParticipantRequest participant : participants) {
			if (participant == null || participant.userId() == null || participant.userId() <= 0) {
				throw new InvalidExpenseException("Participant IDs must be positive integers");
			}
			if (!userIds.add(participant.userId())) {
				throw new InvalidExpenseException("An expense cannot contain duplicate participants");
			}
			creatorIncluded |= participant.userId().equals(creatorUserId);
		}
		if (!creatorIncluded) {
			throw new InvalidExpenseException("The creator must be included as a participant");
		}
	}

	private ExpenseBalanceResponse toBalanceResponse(BalanceKey key, BigDecimal signedAmount) {
		BigDecimal normalized = signedAmount.setScale(MONEY_SCALE, RoundingMode.HALF_UP);
		if (normalized.signum() > 0) {
			return new ExpenseBalanceResponse(key.otherUserId(), key.currency(), normalized, BalanceDirection.CALLER_OWES);
		}
		if (normalized.signum() < 0) {
			return new ExpenseBalanceResponse(key.otherUserId(), key.currency(), normalized.abs(),
					BalanceDirection.THEY_OWE_CALLER);
		}
		return new ExpenseBalanceResponse(key.otherUserId(), key.currency(), normalized, BalanceDirection.SETTLED);
	}

	private void validateUserId(Long userId) {
		if (userId == null || userId <= 0) {
			throw new InvalidExpenseException("X-User-Id must be a positive integer");
		}
	}

	private record BalanceKey(Long otherUserId, String currency) {
	}
}
