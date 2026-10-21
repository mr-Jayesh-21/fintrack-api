package com.fintrack.fintrack_api.expenses;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * A user's allocated share of a shared expense.
 */
@Entity
@Table(name = "expense_participants",
		uniqueConstraints = @UniqueConstraint(name = "uk_expense_participant", columnNames = { "expense_id",
				"user_id" }))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExpenseParticipant {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "expense_id", nullable = false, updatable = false)
	private SharedExpense expense;

	@Column(name = "user_id", nullable = false, updatable = false)
	private Long userId;

	@Column(name = "share_amount", nullable = false, precision = 19, scale = 2)
	private BigDecimal shareAmount;

	/**
	 * Creates a participant with a normalized share.
	 *
	 * @param userId the participant's identifier
	 * @param shareAmount the participant's allocated share
	 */
	public ExpenseParticipant(Long userId, BigDecimal shareAmount) {
		this.userId = userId;
		this.shareAmount = shareAmount;
	}

	void setExpense(SharedExpense expense) {
		this.expense = expense;
	}
}
