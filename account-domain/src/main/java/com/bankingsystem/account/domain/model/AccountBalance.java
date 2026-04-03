package com.bankingsystem.account.domain.model;

import com.bankingsystem.common.persistence.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.math.BigDecimal;

@Entity
@Table(name = "account_balance")
public class AccountBalance extends BaseEntity {

    @Id
    @Column(name = "account_id")
    private Long accountId;

    @MapsId
    @OneToOne(optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(name = "ledger_balance", nullable = false, precision = 18, scale = 2)
    private BigDecimal ledgerBalance;

    @Column(name = "available_balance", nullable = false, precision = 18, scale = 2)
    private BigDecimal availableBalance;

    @Column(name = "hold_amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal holdAmount;

    @Column(name = "last_transaction_id")
    private Long lastTransactionId;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    protected AccountBalance() {
    }

    public AccountBalance(
        Account account,
        BigDecimal ledgerBalance,
        BigDecimal availableBalance,
        BigDecimal holdAmount
    ) {
        this.account = account;
        this.ledgerBalance = ledgerBalance;
        this.availableBalance = availableBalance;
        this.holdAmount = holdAmount;
        account.assignBalance(this);
    }

    public Long getAccountId() {
        return accountId;
    }

    public Account getAccount() {
        return account;
    }

    public BigDecimal getLedgerBalance() {
        return ledgerBalance;
    }

    public BigDecimal getAvailableBalance() {
        return availableBalance;
    }

    public BigDecimal getHoldAmount() {
        return holdAmount;
    }

    public Long getLastTransactionId() {
        return lastTransactionId;
    }

    public Long getVersion() {
        return version;
    }
}
