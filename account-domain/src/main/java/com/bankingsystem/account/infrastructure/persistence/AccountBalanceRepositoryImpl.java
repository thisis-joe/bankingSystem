package com.bankingsystem.account.infrastructure.persistence;

import com.bankingsystem.account.domain.model.AccountBalance;
import com.bankingsystem.account.domain.repository.AccountBalanceRepository;
import com.bankingsystem.account.infrastructure.persistence.jpa.SpringDataAccountBalanceJpaRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class AccountBalanceRepositoryImpl implements AccountBalanceRepository {

    private final SpringDataAccountBalanceJpaRepository springDataAccountBalanceJpaRepository;

    public AccountBalanceRepositoryImpl(SpringDataAccountBalanceJpaRepository springDataAccountBalanceJpaRepository) {
        this.springDataAccountBalanceJpaRepository = springDataAccountBalanceJpaRepository;
    }

    @Override
    public Optional<AccountBalance> findByAccountId(Long accountId) {
        return springDataAccountBalanceJpaRepository.findById(accountId);
    }

    @Override
    public Optional<AccountBalance> findByAccountIdForUpdate(Long accountId) {
        return springDataAccountBalanceJpaRepository.findByAccountIdForUpdate(accountId);
    }

    @Override
    public AccountBalance save(AccountBalance accountBalance) {
        return springDataAccountBalanceJpaRepository.save(accountBalance);
    }
}
