package com.exercise.WalletTransfers.model.postgres;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(
		name = "transactions",
		uniqueConstraints =
				@UniqueConstraint(
						name = "uk_transactions_unique_reference_from_wallet_id",
						columnNames = {"unique_reference", "from_wallet_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Transaction extends GenericData {

	@Column(name = "unique_reference", nullable = false)
	private String uniqueReference;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(
			name = "from_wallet_id",
			nullable = false,
			foreignKey = @ForeignKey(name = "fk_transactions_from_wallet_id"))
	private Wallet fromWallet;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(
			name = "to_wallet_id",
			nullable = false,
			foreignKey = @ForeignKey(name = "fk_transactions_to_wallet_id"))
	private Wallet toWallet;

	@Column(nullable = false)
	private String status;

	@Column(nullable = false)
	private String message;

	@Column(name = "amount_paise", nullable = false)
	private Long amountPaise;
}
