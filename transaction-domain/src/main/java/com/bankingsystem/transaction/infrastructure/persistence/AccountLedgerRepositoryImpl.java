package com.bankingsystem.transaction.infrastructure.persistence;

import com.bankingsystem.transaction.domain.model.AccountLedger;
import com.bankingsystem.transaction.domain.repository.AccountLedgerRepository;
import com.bankingsystem.transaction.infrastructure.persistence.jpa.SpringDataAccountLedgerJpaRepository;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class AccountLedgerRepositoryImpl implements AccountLedgerRepository {

    private final SpringDataAccountLedgerJpaRepository springDataAccountLedgerJpaRepository;

    public AccountLedgerRepositoryImpl(SpringDataAccountLedgerJpaRepository springDataAccountLedgerJpaRepository) {
        this.springDataAccountLedgerJpaRepository = springDataAccountLedgerJpaRepository;
    }

    @Override
    public List<AccountLedger> saveAll(List<AccountLedger> accountLedgers) {
        return springDataAccountLedgerJpaRepository.saveAll(accountLedgers);
    }
}
