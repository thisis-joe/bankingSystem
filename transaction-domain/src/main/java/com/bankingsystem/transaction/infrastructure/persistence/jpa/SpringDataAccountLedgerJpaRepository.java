package com.bankingsystem.transaction.infrastructure.persistence.jpa;

import com.bankingsystem.transaction.domain.model.AccountLedger;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataAccountLedgerJpaRepository extends JpaRepository<AccountLedger, Long> {
}
