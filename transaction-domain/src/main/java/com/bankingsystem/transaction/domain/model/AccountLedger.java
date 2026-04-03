package com.bankingsystem.transaction.domain.model;

import com.bankingsystem.account.domain.model.Account;
import com.bankingsystem.common.persistence.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "account_ledger")
public class AccountLedger extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ledger_id")
    private Long ledgerId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "transaction_id", nullable = false)
    private BankTransaction bankTransaction;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "entry_id", nullable = false)
    private TransactionEntry transactionEntry;

    @Enumerated(EnumType.STRING)
    @Column(name = "ledger_type", nullable = false, length = 30)
    private LedgerType ledgerType;

    @Column(name = "amount_delta", nullable = false, precision = 18, scale = 2)
    private BigDecimal amountDelta;

    @Column(name = "balance_after", nullable = false, precision = 18, scale = 2)
    private BigDecimal balanceAfter;

    @Column(name = "occurred_at", nullable = false)
    private OffsetDateTime occurredAt;

    protected AccountLedger() {
    }

    public AccountLedger(
        Account account,
        BankTransaction bankTransaction,
        TransactionEntry transactionEntry,
        LedgerType ledgerType,
        BigDecimal amountDelta,
        BigDecimal balanceAfter,
        OffsetDateTime occurredAt
    ) {
        this.account = account;
        this.bankTransaction = bankTransaction;
        this.transactionEntry = transactionEntry;
        this.ledgerType = ledgerType;
        this.amountDelta = amountDelta;
        this.balanceAfter = balanceAfter;
        this.occurredAt = occurredAt;
        bankTransaction.addLedger(this);
    }

    public Long getLedgerId() {
        return ledgerId;
    }

    public Account getAccount() {
        return account;
    }

    public BankTransaction getBankTransaction() {
        return bankTransaction;
    }

    public TransactionEntry getTransactionEntry() {
        return transactionEntry;
    }

    public LedgerType getLedgerType() {
        return ledgerType;
    }

    public BigDecimal getAmountDelta() {
        return amountDelta;
    }

    public BigDecimal getBalanceAfter() {
        return balanceAfter;
    }

    public OffsetDateTime getOccurredAt() {
        return occurredAt;
    }
}
