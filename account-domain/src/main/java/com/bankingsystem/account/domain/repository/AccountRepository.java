package com.bankingsystem.account.domain.repository;

import com.bankingsystem.account.domain.model.Account;
import java.util.Optional;

public interface AccountRepository {

    Optional<Account> findByAccountNo(String accountNo);

    Optional<Account> findById(Long accountId);

    Account save(Account account);
}
