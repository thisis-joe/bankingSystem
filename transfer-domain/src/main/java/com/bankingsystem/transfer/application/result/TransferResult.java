package com.bankingsystem.transfer.application.result;

import com.bankingsystem.transaction.domain.model.BankTransaction;
import com.bankingsystem.transaction.domain.model.TransactionStatus;
import com.bankingsystem.transaction.domain.model.TransactionType;
import java.time.LocalDate;
import java.time.OffsetDateTime;

public record TransferResult(
    Long transactionId,
    TransactionType transactionType,
    TransactionStatus transactionStatus,
    String requestId,
    String idempotencyKey,
    LocalDate businessDate,
    OffsetDateTime occurredAt,
    OffsetDateTime postedAt,
    String description
) {

    public static TransferResult from(BankTransaction bankTransaction) {
        return new TransferResult(
            bankTransaction.getTransactionId(),
            bankTransaction.getTransactionType(),
            bankTransaction.getTransactionStatus(),
            bankTransaction.getRequestId(),
            bankTransaction.getIdempotencyKey(),
            bankTransaction.getBusinessDate(),
            bankTransaction.getOccurredAt(),
            bankTransaction.getPostedAt(),
            bankTransaction.getDescription()
        );
    }
}
