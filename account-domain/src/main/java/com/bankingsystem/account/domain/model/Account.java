package com.bankingsystem.account.domain.model;

import com.bankingsystem.common.persistence.BaseEntity;
import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "account")
public class Account extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long accountId;

    @Column(name = "account_no", nullable = false, length = 30, unique = true)
    private String accountNo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "currency_code", nullable = false, length = 3)
    private String currencyCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private AccountStatus status;

    @Column(name = "opened_at", nullable = false)
    private OffsetDateTime openedAt;

    @Column(name = "closed_at")
    private OffsetDateTime closedAt;

    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private AccountBalance accountBalance;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<AccountHolder> holders = new ArrayList<>();

    protected Account() {
    }

    public Account(
        String accountNo,
        Product product,
        String currencyCode,
        AccountStatus status,
        OffsetDateTime openedAt
    ) {
        this.accountNo = accountNo;
        this.product = product;
        this.currencyCode = currencyCode;
        this.status = status;
        this.openedAt = openedAt;
    }

    public void assignBalance(AccountBalance accountBalance) {
        this.accountBalance = accountBalance;
    }

    public void addHolder(AccountHolder holder) {
        this.holders.add(holder);
    }

    public Long getAccountId() {
        return accountId;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public Product getProduct() {
        return product;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public OffsetDateTime getOpenedAt() {
        return openedAt;
    }

    public OffsetDateTime getClosedAt() {
        return closedAt;
    }

    public AccountBalance getAccountBalance() {
        return accountBalance;
    }

    public List<AccountHolder> getHolders() {
        return Collections.unmodifiableList(holders);
    }
}
