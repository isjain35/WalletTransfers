package com.exercise.WalletTransfers.model.postgres;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(
		name = "wallets",
		uniqueConstraints = @UniqueConstraint(name = "uk_wallets_reference_id_user_id", columnNames = {"user_id", "reference_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Wallet extends GenericData {

	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(
			name = "user_id",
			nullable = false,
			unique = true,
			foreignKey = @ForeignKey(name = "fk_wallets_user_id"))
	private User user;

	@Column(name = "reference_id", nullable = false)
	private String referenceId;

	@Column(nullable = false)
	private Long balance = 0L;
}
