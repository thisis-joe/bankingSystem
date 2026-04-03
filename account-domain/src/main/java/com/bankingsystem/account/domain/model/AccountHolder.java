package com.bankingsystem.account.domain.model;

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

@Entity
@Table(name = "account_holder")
public class AccountHolder extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_holder_id")
    private Long accountHolderId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Enumerated(EnumType.STRING)
    @Column(name = "holder_role", nullable = false, length = 30)
    private AccountHolderRole holderRole;

    @Column(name = "is_primary", nullable = false)
    private boolean primary;

    protected AccountHolder() {
    }

    public AccountHolder(Account account, Customer customer, AccountHolderRole holderRole, boolean primary) {
        this.account = account;
        this.customer = customer;
        this.holderRole = holderRole;
        this.primary = primary;
    }

    public Long getAccountHolderId() {
        return accountHolderId;
    }

    public Account getAccount() {
        return account;
    }

    public Customer getCustomer() {
        return customer;
    }

    public AccountHolderRole getHolderRole() {
        return holderRole;
    }

    public boolean isPrimary() {
        return primary;
    }
}
