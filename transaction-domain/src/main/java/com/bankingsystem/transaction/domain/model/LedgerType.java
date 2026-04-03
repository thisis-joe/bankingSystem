package com.bankingsystem.transaction.domain.model;

public enum LedgerType {
    DEPOSIT,
    WITHDRAWAL,
    TRANSFER_OUT,
    TRANSFER_IN,
    INTEREST,
    FEE,
    REVERSAL,
    ADJUSTMENT
}
