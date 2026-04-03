package com.bankingsystem.transaction.infrastructure.persistence.jpa;

import com.bankingsystem.transaction.domain.model.BankTransaction;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataBankTransactionJpaRepository extends JpaRepository<BankTransaction, Long> {

    Optional<BankTransaction> findByRequestId(String requestId);

    Optional<BankTransaction> findByIdempotencyKey(String idempotencyKey);
}
