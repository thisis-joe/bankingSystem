package com.bankingsystem.account.infrastructure.persistence.jpa;

import com.bankingsystem.account.domain.model.Account;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataAccountJpaRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByAccountNo(String accountNo);
}
