package com.exercise.WalletTransfers.model.postgres;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionWrapper {
    private String sentTo;
    private Long amountPaise;
    private String status;
    private String message;
}
