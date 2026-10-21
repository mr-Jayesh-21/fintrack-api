package com.fintrack.fintrack_api.expenses;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * An expense created by one user and shared among its participants.
 */
@Entity
@Table(name = "shared_expenses")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SharedExpense {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "creator_user_id", nullable = false, updatable = false)
	private Long creatorUserId;

	@Column(nullable = false, length = 255)
	private String description;

	@Column(name = "total_amount", nullable = false, precision = 19, scale = 2)
	private BigDecimal totalAmount;

	@Column(nullable = false, length = 3)
	private String currency;

	@Enumerated(EnumType.STRING)
	@Column(name = "split_type", nullable = false, length = 16)
	private SplitType splitType;

	@OneToMany(mappedBy = "expense", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<ExpenseParticipant> participants = new ArrayList<>();

	@Column(nullable = false, updatable = false)
	private Instant createdAt;

	/**
	 * Creates a shared expense with prevalidated and normalized values.
	 *
	 * @param creatorUserId the user who created and paid the expense
	 * @param description the expense description
	 * @param totalAmount the normalized total amount
	 * @param currency the ISO 4217 currency code
	 * @param splitType the allocation rule
	 */
	public SharedExpense(Long creatorUserId, String description, BigDecimal totalAmount, String currency,
			SplitType splitType) {
		this.creatorUserId = creatorUserId;
		this.description = description;
		this.totalAmount = totalAmount;
		this.currency = currency;
		this.splitType = splitType;
	}

	/**
	 * Adds a participant to this expense and establishes the owning relationship.
	 *
	 * @param participant the participant to associate
	 */
	public void addParticipant(ExpenseParticipant participant) {
		participants.add(participant);
		participant.setExpense(this);
	}

	@PrePersist
	private void setCreatedAtIfMissing() {
		if (createdAt == null) {
			createdAt = Instant.now();
		}
	}
}
