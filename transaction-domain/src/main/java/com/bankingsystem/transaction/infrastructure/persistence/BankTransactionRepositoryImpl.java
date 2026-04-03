package com.bankingsystem.transaction.infrastructure.persistence;

import com.bankingsystem.transaction.domain.model.BankTransaction;
import com.bankingsystem.transaction.domain.repository.BankTransactionRepository;
import com.bankingsystem.transaction.infrastructure.persistence.jpa.SpringDataBankTransactionJpaRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class BankTransactionRepositoryImpl implements BankTransactionRepository {

    private final SpringDataBankTransactionJpaRepository springDataBankTransactionJpaRepository;

    public BankTransactionRepositoryImpl(SpringDataBankTransactionJpaRepository springDataBankTransactionJpaRepository) {
        this.springDataBankTransactionJpaRepository = springDataBankTransactionJpaRepository;
    }

    @Override
    public Optional<BankTransaction> findByRequestId(String requestId) {
        return springDataBankTransactionJpaRepository.findByRequestId(requestId);
    }

    @Override
    public Optional<BankTransaction> findByIdempotencyKey(String idempotencyKey) {
        return springDataBankTransactionJpaRepository.findByIdempotencyKey(idempotencyKey);
    }

    @Override
    public BankTransaction save(BankTransaction bankTransaction) {
        return springDataBankTransactionJpaRepository.save(bankTransaction);
    }
}
