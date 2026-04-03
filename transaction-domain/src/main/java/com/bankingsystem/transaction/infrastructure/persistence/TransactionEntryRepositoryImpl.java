package com.bankingsystem.transaction.infrastructure.persistence;

import com.bankingsystem.transaction.domain.model.TransactionEntry;
import com.bankingsystem.transaction.domain.repository.TransactionEntryRepository;
import com.bankingsystem.transaction.infrastructure.persistence.jpa.SpringDataTransactionEntryJpaRepository;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class TransactionEntryRepositoryImpl implements TransactionEntryRepository {

    private final SpringDataTransactionEntryJpaRepository springDataTransactionEntryJpaRepository;

    public TransactionEntryRepositoryImpl(SpringDataTransactionEntryJpaRepository springDataTransactionEntryJpaRepository) {
        this.springDataTransactionEntryJpaRepository = springDataTransactionEntryJpaRepository;
    }

    @Override
    public List<TransactionEntry> saveAll(List<TransactionEntry> transactionEntries) {
        return springDataTransactionEntryJpaRepository.saveAll(transactionEntries);
    }
}
