package com.bankingsystem.account.domain.repository;

import com.bankingsystem.account.domain.model.AccountBalance;
import java.util.Optional;

public interface AccountBalanceRepository {

    Optional<AccountBalance> findByAccountId(Long accountId);

    Optional<AccountBalance> findByAccountIdForUpdate(Long accountId);

    AccountBalance save(AccountBalance accountBalance);
}
