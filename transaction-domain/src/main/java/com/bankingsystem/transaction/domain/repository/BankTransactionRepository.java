package com.bankingsystem.transaction.domain.repository;

import com.bankingsystem.transaction.domain.model.BankTransaction;
import java.util.Optional;

public interface BankTransactionRepository {

    Optional<BankTransaction> findByRequestId(String requestId);

    Optional<BankTransaction> findByIdempotencyKey(String idempotencyKey);

    BankTransaction save(BankTransaction bankTransaction);
}
