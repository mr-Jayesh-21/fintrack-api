package com.fintrack.fintrack_api.transactions;

import java.math.BigDecimal;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "transactions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Transaction {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Positive
	@Column(nullable = false)
	private Long userId;

	@NotNull
	@Positive
	@Column(nullable = false, precision = 19, scale = 4)
	private BigDecimal amount;

	@NotBlank
	@Column(nullable = false)
	private String description;

	@NotBlank
	@Column(nullable = false)
	private String category;

	@Column(nullable = false, updatable = false)
	private Instant createdAt;

	/**
	 * Creates a transaction before it is persisted.
	 *
	 * @param userId the owner of the transaction
	 * @param amount the transaction amount
	 * @param description the transaction description
	 * @param category the transaction category
	 */
	public Transaction(Long userId, BigDecimal amount, String description, String category) {
		this.userId = userId;
		this.amount = amount;
		this.description = description;
		this.category = category;
	}

	@PrePersist
	private void setCreatedAtIfMissing() {
		if (createdAt == null) {
			createdAt = Instant.now();
		}
	}
}
