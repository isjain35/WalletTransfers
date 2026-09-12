package com.exercise.WalletTransfers.repository;

import com.exercise.WalletTransfers.model.postgres.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Transaction getByUniqueReferenceAndFromWalletUserId(String uniqueReference, Long id);
}
