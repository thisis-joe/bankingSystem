package com.bankingsystem.transfer.application.support;

import com.bankingsystem.common.exception.IdempotencyConflictException;
import com.bankingsystem.transaction.domain.model.BankTransaction;
import com.bankingsystem.transaction.domain.model.TransactionType;
import com.bankingsystem.transaction.domain.repository.BankTransactionRepository;
import java.util.Objects;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class TransactionIdempotencyResolver {

    private final BankTransactionRepository bankTransactionRepository;

    public TransactionIdempotencyResolver(BankTransactionRepository bankTransactionRepository) {
        this.bankTransactionRepository = bankTransactionRepository;
    }

    public Optional<BankTransaction> resolve(
        String requestId,
        String idempotencyKey,
        TransactionType expectedTransactionType
    ) {
        Optional<BankTransaction> requestTransaction = hasText(requestId)
            ? bankTransactionRepository.findByRequestId(requestId)
            : Optional.empty();

        Optional<BankTransaction> idempotencyTransaction = hasText(idempotencyKey)
            ? bankTransactionRepository.findByIdempotencyKey(idempotencyKey)
            : Optional.empty();

        if (requestTransaction.isPresent() && idempotencyTransaction.isPresent()) {
            BankTransaction requestMatched = requestTransaction.get();
            BankTransaction idempotencyMatched = idempotencyTransaction.get();

            if (!Objects.equals(requestMatched.getTransactionId(), idempotencyMatched.getTransactionId())) {
                throw new IdempotencyConflictException(
                    "requestId 와 idempotencyKey 가 서로 다른 거래를 가리킵니다."
                );
            }
        }

        Optional<BankTransaction> resolved = requestTransaction.isPresent()
            ? requestTransaction
            : idempotencyTransaction;

        resolved.ifPresent(transaction -> validateTransactionType(transaction, expectedTransactionType));
        return resolved;
    }

    private void validateTransactionType(
        BankTransaction bankTransaction,
        TransactionType expectedTransactionType
    ) {
        if (bankTransaction.getTransactionType() != expectedTransactionType) {
            throw new IdempotencyConflictException(
                "같은 요청 식별자 또는 멱등 키가 다른 거래 유형에 재사용되었습니다. "
                    + "existingType=" + bankTransaction.getTransactionType().name()
                    + ", requestedType=" + expectedTransactionType.name()
            );
        }
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
