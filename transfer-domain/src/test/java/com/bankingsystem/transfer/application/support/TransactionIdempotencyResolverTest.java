package com.bankingsystem.transfer.application.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.bankingsystem.common.exception.IdempotencyConflictException;
import com.bankingsystem.transaction.domain.model.BankTransaction;
import com.bankingsystem.transaction.domain.model.ChannelType;
import com.bankingsystem.transaction.domain.model.TransactionStatus;
import com.bankingsystem.transaction.domain.model.TransactionType;
import com.bankingsystem.transaction.domain.repository.BankTransactionRepository;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class TransactionIdempotencyResolverTest {

    @Mock
    private BankTransactionRepository bankTransactionRepository;

    private TransactionIdempotencyResolver transactionIdempotencyResolver;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        transactionIdempotencyResolver = new TransactionIdempotencyResolver(bankTransactionRepository);
    }

    @Test
    void 같은RequestId가있으면기존거래를반환한다() throws Exception {
        BankTransaction existingTransaction = transaction(1L, TransactionType.DEPOSIT, "REQ-1", "IDEMP-1");
        when(bankTransactionRepository.findByRequestId("REQ-1")).thenReturn(Optional.of(existingTransaction));
        when(bankTransactionRepository.findByIdempotencyKey("IDEMP-1")).thenReturn(Optional.of(existingTransaction));

        BankTransaction resolved = transactionIdempotencyResolver
            .resolve("REQ-1", "IDEMP-1", TransactionType.DEPOSIT)
            .orElseThrow();

        assertEquals(1L, resolved.getTransactionId());
    }

    @Test
    void 같은멱등키를다른거래유형에재사용하면충돌예외가발생한다() throws Exception {
        BankTransaction existingTransaction = transaction(1L, TransactionType.DEPOSIT, "REQ-1", "IDEMP-1");
        when(bankTransactionRepository.findByRequestId("REQ-2")).thenReturn(Optional.empty());
        when(bankTransactionRepository.findByIdempotencyKey("IDEMP-1")).thenReturn(Optional.of(existingTransaction));

        assertThrows(
            IdempotencyConflictException.class,
            () -> transactionIdempotencyResolver.resolve("REQ-2", "IDEMP-1", TransactionType.TRANSFER)
        );
    }

    @Test
    void requestId와멱등키가서로다른거래를가리키면충돌예외가발생한다() throws Exception {
        BankTransaction requestTransaction = transaction(1L, TransactionType.TRANSFER, "REQ-1", "IDEMP-1");
        BankTransaction idempotencyTransaction = transaction(2L, TransactionType.TRANSFER, "REQ-2", "IDEMP-1");

        when(bankTransactionRepository.findByRequestId("REQ-1")).thenReturn(Optional.of(requestTransaction));
        when(bankTransactionRepository.findByIdempotencyKey("IDEMP-1")).thenReturn(Optional.of(idempotencyTransaction));

        assertThrows(
            IdempotencyConflictException.class,
            () -> transactionIdempotencyResolver.resolve("REQ-1", "IDEMP-1", TransactionType.TRANSFER)
        );
    }

    private BankTransaction transaction(
        Long transactionId,
        TransactionType transactionType,
        String requestId,
        String idempotencyKey
    ) throws Exception {
        BankTransaction bankTransaction = new BankTransaction(
            transactionType,
            TransactionStatus.POSTED,
            requestId,
            idempotencyKey,
            ChannelType.OPEN_API,
            LocalDate.now(),
            OffsetDateTime.now(),
            "test"
        );

        Field field = BankTransaction.class.getDeclaredField("transactionId");
        field.setAccessible(true);
        field.set(bankTransaction, transactionId);
        return bankTransaction;
    }
}
