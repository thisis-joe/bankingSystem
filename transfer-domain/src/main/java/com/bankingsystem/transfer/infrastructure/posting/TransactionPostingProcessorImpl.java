package com.bankingsystem.transfer.infrastructure.posting;

import com.bankingsystem.account.domain.model.Account;
import com.bankingsystem.account.domain.model.AccountBalance;
import com.bankingsystem.account.domain.repository.AccountBalanceRepository;
import com.bankingsystem.transaction.domain.model.AccountLedger;
import com.bankingsystem.transaction.domain.model.BankTransaction;
import com.bankingsystem.transaction.domain.model.EntryType;
import com.bankingsystem.transaction.domain.model.LedgerType;
import com.bankingsystem.transaction.domain.model.TransactionEntry;
import com.bankingsystem.transaction.domain.repository.AccountLedgerRepository;
import com.bankingsystem.transaction.domain.repository.TransactionEntryRepository;
import com.bankingsystem.transfer.application.port.out.TransactionPostingProcessor;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class TransactionPostingProcessorImpl implements TransactionPostingProcessor {

    private static final int SCALE = 2;

    private final AccountBalanceRepository accountBalanceRepository;
    private final TransactionEntryRepository transactionEntryRepository;
    private final AccountLedgerRepository accountLedgerRepository;

    public TransactionPostingProcessorImpl(
        AccountBalanceRepository accountBalanceRepository,
        TransactionEntryRepository transactionEntryRepository,
        AccountLedgerRepository accountLedgerRepository
    ) {
        this.accountBalanceRepository = accountBalanceRepository;
        this.transactionEntryRepository = transactionEntryRepository;
        this.accountLedgerRepository = accountLedgerRepository;
    }

    @Override
    public void postDeposit(
        BankTransaction bankTransaction,
        Account account,
        AccountBalance accountBalance,
        BigDecimal amount,
        String currencyCode
    ) {
        BigDecimal normalizedAmount = normalize(amount);
        accountBalance.deposit(normalizedAmount, bankTransaction.getTransactionId());

        TransactionEntry entry = new TransactionEntry(
            bankTransaction,
            account,
            EntryType.CREDIT,
            normalizedAmount,
            currencyCode,
            accountBalance.getLedgerBalance(),
            1
        );

        transactionEntryRepository.saveAll(List.of(entry));
        accountLedgerRepository.saveAll(List.of(new AccountLedger(
            account,
            bankTransaction,
            entry,
            LedgerType.DEPOSIT,
            normalizedAmount,
            accountBalance.getLedgerBalance(),
            bankTransaction.getOccurredAt()
        )));
        accountBalanceRepository.save(accountBalance);
    }

    @Override
    public void postWithdrawal(
        BankTransaction bankTransaction,
        Account account,
        AccountBalance accountBalance,
        BigDecimal amount,
        String currencyCode
    ) {
        BigDecimal normalizedAmount = normalize(amount);
        accountBalance.withdraw(normalizedAmount, bankTransaction.getTransactionId());

        TransactionEntry entry = new TransactionEntry(
            bankTransaction,
            account,
            EntryType.DEBIT,
            normalizedAmount,
            currencyCode,
            accountBalance.getLedgerBalance(),
            1
        );

        transactionEntryRepository.saveAll(List.of(entry));
        accountLedgerRepository.saveAll(List.of(new AccountLedger(
            account,
            bankTransaction,
            entry,
            LedgerType.WITHDRAWAL,
            normalizedAmount.negate(),
            accountBalance.getLedgerBalance(),
            bankTransaction.getOccurredAt()
        )));
        accountBalanceRepository.save(accountBalance);
    }

    @Override
    public void postTransfer(
        BankTransaction bankTransaction,
        Account sourceAccount,
        AccountBalance sourceAccountBalance,
        Account destinationAccount,
        AccountBalance destinationAccountBalance,
        BigDecimal amount,
        String currencyCode
    ) {
        BigDecimal normalizedAmount = normalize(amount);

        sourceAccountBalance.withdraw(normalizedAmount, bankTransaction.getTransactionId());
        destinationAccountBalance.deposit(normalizedAmount, bankTransaction.getTransactionId());

        TransactionEntry sourceEntry = new TransactionEntry(
            bankTransaction,
            sourceAccount,
            EntryType.DEBIT,
            normalizedAmount,
            currencyCode,
            sourceAccountBalance.getLedgerBalance(),
            1
        );

        TransactionEntry destinationEntry = new TransactionEntry(
            bankTransaction,
            destinationAccount,
            EntryType.CREDIT,
            normalizedAmount,
            currencyCode,
            destinationAccountBalance.getLedgerBalance(),
            2
        );

        transactionEntryRepository.saveAll(List.of(sourceEntry, destinationEntry));
        accountLedgerRepository.saveAll(List.of(
            new AccountLedger(
                sourceAccount,
                bankTransaction,
                sourceEntry,
                LedgerType.TRANSFER_OUT,
                normalizedAmount.negate(),
                sourceAccountBalance.getLedgerBalance(),
                bankTransaction.getOccurredAt()
            ),
            new AccountLedger(
                destinationAccount,
                bankTransaction,
                destinationEntry,
                LedgerType.TRANSFER_IN,
                normalizedAmount,
                destinationAccountBalance.getLedgerBalance(),
                bankTransaction.getOccurredAt()
            )
        ));
        accountBalanceRepository.save(sourceAccountBalance);
        accountBalanceRepository.save(destinationAccountBalance);
    }

    private BigDecimal normalize(BigDecimal amount) {
        return amount.setScale(SCALE, RoundingMode.HALF_UP);
    }
}
