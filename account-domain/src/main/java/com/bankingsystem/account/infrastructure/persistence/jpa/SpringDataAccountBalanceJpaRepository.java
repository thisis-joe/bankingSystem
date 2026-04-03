package com.bankingsystem.account.infrastructure.persistence.jpa;

import com.bankingsystem.account.domain.model.AccountBalance;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpringDataAccountBalanceJpaRepository extends JpaRepository<AccountBalance, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select balance from AccountBalance balance where balance.accountId = :accountId")
    Optional<AccountBalance> findByAccountIdForUpdate(@Param("accountId") Long accountId);
}
