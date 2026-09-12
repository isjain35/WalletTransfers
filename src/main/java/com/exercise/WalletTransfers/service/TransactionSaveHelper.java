package com.exercise.WalletTransfers.service;

import com.exercise.WalletTransfers.model.postgres.Transaction;
import com.exercise.WalletTransfers.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TransactionSaveHelper {

    @Autowired
    private TransactionRepository transactionRepository;

    // REQUIRES_NEW opens an isolated transaction block
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveAndFlushIsolated(Transaction transaction) {
        transactionRepository.saveAndFlush(transaction);
    }
}
