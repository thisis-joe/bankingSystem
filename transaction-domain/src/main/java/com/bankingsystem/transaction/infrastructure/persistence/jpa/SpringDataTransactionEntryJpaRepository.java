package com.bankingsystem.transaction.infrastructure.persistence.jpa;

import com.bankingsystem.transaction.domain.model.TransactionEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataTransactionEntryJpaRepository extends JpaRepository<TransactionEntry, Long> {
}
