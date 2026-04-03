package com.bankingsystem.account.infrastructure.persistence;

import com.bankingsystem.account.domain.model.Account;
import com.bankingsystem.account.domain.repository.AccountRepository;
import com.bankingsystem.account.infrastructure.persistence.jpa.SpringDataAccountJpaRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class AccountRepositoryImpl implements AccountRepository {

    private final SpringDataAccountJpaRepository springDataAccountJpaRepository;

    public AccountRepositoryImpl(SpringDataAccountJpaRepository springDataAccountJpaRepository) {
        this.springDataAccountJpaRepository = springDataAccountJpaRepository;
    }

    @Override
    public Optional<Account> findByAccountNo(String accountNo) {
        return springDataAccountJpaRepository.findByAccountNo(accountNo);
    }

    @Override
    public Optional<Account> findById(Long accountId) {
        return springDataAccountJpaRepository.findById(accountId);
    }

    @Override
    public Account save(Account account) {
        return springDataAccountJpaRepository.save(account);
    }
}
