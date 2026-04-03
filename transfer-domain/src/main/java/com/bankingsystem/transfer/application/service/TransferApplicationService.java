package com.bankingsystem.transfer.application.service;

import com.bankingsystem.account.domain.model.Account;
import com.bankingsystem.account.domain.model.AccountBalance;
import com.bankingsystem.account.domain.repository.AccountBalanceRepository;
import com.bankingsystem.account.domain.repository.AccountRepository;
import com.bankingsystem.common.exception.AccountBalanceNotFoundException;
import com.bankingsystem.common.exception.AccountNotFoundException;
import com.bankingsystem.common.exception.CurrencyMismatchException;
import com.bankingsystem.common.exception.InsufficientBalanceException;
import com.bankingsystem.common.exception.InvalidAmountException;
import com.bankingsystem.common.exception.SameAccountTransferException;
import com.bankingsystem.transaction.domain.model.BankTransaction;
import com.bankingsystem.transaction.domain.model.TransactionStatus;
import com.bankingsystem.transaction.domain.model.TransactionType;
import com.bankingsystem.transaction.domain.repository.BankTransactionRepository;
import com.bankingsystem.transfer.application.command.DepositCommand;
import com.bankingsystem.transfer.application.command.TransferCommand;
import com.bankingsystem.transfer.application.command.WithdrawalCommand;
import com.bankingsystem.transfer.application.port.out.TransactionPostingProcessor;
import com.bankingsystem.transfer.application.result.TransferResult;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransferApplicationService {

    private final AccountRepository accountRepository;
    private final AccountBalanceRepository accountBalanceRepository;
    private final BankTransactionRepository bankTransactionRepository;
    private final TransactionPostingProcessor transactionPostingProcessor;

    public TransferApplicationService(
        AccountRepository accountRepository,
        AccountBalanceRepository accountBalanceRepository,
        BankTransactionRepository bankTransactionRepository,
        TransactionPostingProcessor transactionPostingProcessor
    ) {
        this.accountRepository = accountRepository;
        this.accountBalanceRepository = accountBalanceRepository;
        this.bankTransactionRepository = bankTransactionRepository;
        this.transactionPostingProcessor = transactionPostingProcessor;
    }

    @Transactional
    public TransferResult deposit(DepositCommand command) {
        validateAmount(command.amount());

        Account account = loadAccount(command.accountNo());
        AccountBalance accountBalance = loadBalanceForUpdate(account.getAccountId());
        validateCurrency(account.getCurrencyCode(), command.currencyCode());

        BankTransaction bankTransaction = bankTransactionRepository.save(createTransaction(
            TransactionType.DEPOSIT,
            command.requestId(),
            command.idempotencyKey(),
            command.channelType(),
            command.description()
        ));

        transactionPostingProcessor.postDeposit(
            bankTransaction,
            account,
            accountBalance,
            command.amount(),
            command.currencyCode()
        );

        bankTransaction.markPosted(OffsetDateTime.now());
        return TransferResult.from(bankTransactionRepository.save(bankTransaction));
    }

    @Transactional
    public TransferResult withdraw(WithdrawalCommand command) {
        validateAmount(command.amount());

        Account account = loadAccount(command.accountNo());
        AccountBalance accountBalance = loadBalanceForUpdate(account.getAccountId());
        validateCurrency(account.getCurrencyCode(), command.currencyCode());
        validateSufficientBalance(accountBalance, command.amount());

        BankTransaction bankTransaction = bankTransactionRepository.save(createTransaction(
            TransactionType.WITHDRAWAL,
            command.requestId(),
            command.idempotencyKey(),
            command.channelType(),
            command.description()
        ));

        transactionPostingProcessor.postWithdrawal(
            bankTransaction,
            account,
            accountBalance,
            command.amount(),
            command.currencyCode()
        );

        bankTransaction.markPosted(OffsetDateTime.now());
        return TransferResult.from(bankTransactionRepository.save(bankTransaction));
    }

    @Transactional
    public TransferResult transfer(TransferCommand command) {
        validateAmount(command.amount());

        Account sourceAccount = loadAccount(command.sourceAccountNo());
        Account destinationAccount = loadAccount(command.destinationAccountNo());

        if (sourceAccount.getAccountId().equals(destinationAccount.getAccountId())) {
            throw new SameAccountTransferException();
        }

        AccountBalance sourceBalance = loadBalanceForUpdate(sourceAccount.getAccountId());
        AccountBalance destinationBalance = loadBalanceForUpdate(destinationAccount.getAccountId());

        validateCurrency(sourceAccount.getCurrencyCode(), command.currencyCode());
        validateCurrency(destinationAccount.getCurrencyCode(), command.currencyCode());
        validateSufficientBalance(sourceBalance, command.amount());

        BankTransaction bankTransaction = bankTransactionRepository.save(createTransaction(
            TransactionType.TRANSFER,
            command.requestId(),
            command.idempotencyKey(),
            command.channelType(),
            command.description()
        ));

        transactionPostingProcessor.postTransfer(
            bankTransaction,
            sourceAccount,
            sourceBalance,
            destinationAccount,
            destinationBalance,
            command.amount(),
            command.currencyCode()
        );

        bankTransaction.markPosted(OffsetDateTime.now());
        return TransferResult.from(bankTransactionRepository.save(bankTransaction));
    }

    private Account loadAccount(String accountNo) {
        return accountRepository.findByAccountNo(accountNo)
            .orElseThrow(() -> new AccountNotFoundException(accountNo));
    }

    private AccountBalance loadBalanceForUpdate(Long accountId) {
        return accountBalanceRepository.findByAccountIdForUpdate(accountId)
            .orElseThrow(() -> new AccountBalanceNotFoundException(accountId));
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new InvalidAmountException();
        }
    }

    private void validateCurrency(String accountCurrencyCode, String commandCurrencyCode) {
        if (!accountCurrencyCode.equals(commandCurrencyCode)) {
            throw new CurrencyMismatchException(accountCurrencyCode, commandCurrencyCode);
        }
    }

    private void validateSufficientBalance(AccountBalance accountBalance, BigDecimal amount) {
        if (accountBalance.getAvailableBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException();
        }
    }

    private BankTransaction createTransaction(
            TransactionType transactionType,
            String requestId,
            String idempotencyKey,
            com.bankingsystem.transaction.domain.model.ChannelType channelType,
            String description
    ) {
        return new BankTransaction(
            transactionType,
            TransactionStatus.REQUESTED,
            requestId,
            idempotencyKey,
            channelType,
            LocalDate.now(),
            OffsetDateTime.now(),
            description
        );
    }
}
