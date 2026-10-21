package com.fintrack.fintrack_api.transactions;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
@Entity
@Table(name = "transactions", indexes = @Index(name = "idx_transactions_user_id", columnList = "user_id"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Transaction {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "user_id", nullable = false, updatable = false)
	private Long userId;
	@Column(nullable = false, precision = 19, scale = 2)
	private BigDecimal amount;
	@Column(nullable = false, length = 3)
	private String currency;
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 16)
	private TransactionType type;
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 32)
	private TransactionCategory category;
	@Column(nullable = false, length = 255)
	private String description;
	@Version
	@Column(nullable = false)
	private Long version;
	@Column(nullable = false, updatable = false)
	private Instant createdAt;
	/**
	 * Creates a transaction with a normalized two-decimal monetary amount.
	 *
	 * @param userId the transaction owner's identifier
	 * @param amount the transaction amount
	 * @param currency the ISO 4217 currency code
	 * @param type whether the transaction is income or expense
	 * @param category the transaction category
	 * @param description the transaction description
	 */
	public Transaction(Long userId, BigDecimal amount, String currency, TransactionType type,
			TransactionCategory category, String description) {
		this.userId = userId;
		this.amount = amount.setScale(2, RoundingMode.HALF_UP);
		this.currency = currency;
		this.type = type;
		this.category = category;
		this.description = description;
	}
	@PrePersist
	private void setCreatedAtIfMissing() {
		if (createdAt == null) {
			createdAt = Instant.now();
		}
	}
}
