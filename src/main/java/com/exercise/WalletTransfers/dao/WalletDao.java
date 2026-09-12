package com.exercise.WalletTransfers.dao;

import org.springframework.stereotype.Repository;

@Repository
public interface WalletDao {
    int debitWallet(Long id, Long amount);
    int creditWallet(Long id, Long amount);
}
