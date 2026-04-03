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

@Entity
@Table(name = "transaction_entry")
public class TransactionEntry extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "entry_id")
    private Long entryId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "transaction_id", nullable = false)
    private BankTransaction bankTransaction;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Enumerated(EnumType.STRING)
    @Column(name = "entry_type", nullable = false, length = 30)
    private EntryType entryType;

    @Column(name = "amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;

    @Column(name = "currency_code", nullable = false, length = 3)
    private String currencyCode;

    @Column(name = "resulting_balance", nullable = false, precision = 18, scale = 2)
    private BigDecimal resultingBalance;

    @Column(name = "sequence_no", nullable = false)
    private Integer sequenceNo;

    protected TransactionEntry() {
    }

    public TransactionEntry(
        BankTransaction bankTransaction,
        Account account,
        EntryType entryType,
        BigDecimal amount,
        String currencyCode,
        BigDecimal resultingBalance,
        Integer sequenceNo
    ) {
        this.bankTransaction = bankTransaction;
        this.account = account;
        this.entryType = entryType;
        this.amount = amount;
        this.currencyCode = currencyCode;
        this.resultingBalance = resultingBalance;
        this.sequenceNo = sequenceNo;
        bankTransaction.addEntry(this);
    }

    public Long getEntryId() {
        return entryId;
    }

    public BankTransaction getBankTransaction() {
        return bankTransaction;
    }

    public Account getAccount() {
        return account;
    }

    public EntryType getEntryType() {
        return entryType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public BigDecimal getResultingBalance() {
        return resultingBalance;
    }

    public Integer getSequenceNo() {
        return sequenceNo;
    }
}
