package com.exercise.WalletTransfers.repository;

import com.exercise.WalletTransfers.model.postgres.User;
import com.exercise.WalletTransfers.model.postgres.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Wallet getByUserId(Long userId);
    Wallet getByUserAndReferenceId(User user, String referenceId);
    Wallet getByUserUsername(String username);
}