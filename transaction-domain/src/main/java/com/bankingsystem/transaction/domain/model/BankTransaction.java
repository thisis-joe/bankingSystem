package com.bankingsystem.transaction.domain.model;

import com.bankingsystem.common.persistence.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "bank_transaction")
public class BankTransaction extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Long transactionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false, length = 30)
    private TransactionType transactionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_status", nullable = false, length = 20)
    private TransactionStatus transactionStatus;

    @Column(name = "request_id", length = 100, unique = true)
    private String requestId;

    @Column(name = "idempotency_key", length = 100, unique = true)
    private String idempotencyKey;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel_type", length = 30)
    private ChannelType channelType;

    @Column(name = "business_date", nullable = false)
    private LocalDate businessDate;

    @Column(name = "occurred_at", nullable = false)
    private OffsetDateTime occurredAt;

    @Column(name = "posted_at")
    private OffsetDateTime postedAt;

    @Column(name = "description", length = 255)
    private String description;

    @OneToMany(mappedBy = "bankTransaction", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<TransactionEntry> entries = new ArrayList<>();

    @OneToMany(mappedBy = "bankTransaction", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<AccountLedger> ledgers = new ArrayList<>();

    protected BankTransaction() {
    }

    public BankTransaction(
        TransactionType transactionType,
        TransactionStatus transactionStatus,
        String requestId,
        String idempotencyKey,
        ChannelType channelType,
        LocalDate businessDate,
        OffsetDateTime occurredAt,
        String description
    ) {
        this.transactionType = transactionType;
        this.transactionStatus = transactionStatus;
        this.requestId = requestId;
        this.idempotencyKey = idempotencyKey;
        this.channelType = channelType;
        this.businessDate = businessDate;
        this.occurredAt = occurredAt;
        this.description = description;
    }

    public void markPosted(OffsetDateTime postedAt) {
        this.transactionStatus = TransactionStatus.POSTED;
        this.postedAt = postedAt;
    }

    public void addEntry(TransactionEntry entry) {
        this.entries.add(entry);
    }

    public void addLedger(AccountLedger ledger) {
        this.ledgers.add(ledger);
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public TransactionStatus getTransactionStatus() {
        return transactionStatus;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public ChannelType getChannelType() {
        return channelType;
    }

    public LocalDate getBusinessDate() {
        return businessDate;
    }

    public OffsetDateTime getOccurredAt() {
        return occurredAt;
    }

    public OffsetDateTime getPostedAt() {
        return postedAt;
    }

    public String getDescription() {
        return description;
    }

    public List<TransactionEntry> getEntries() {
        return Collections.unmodifiableList(entries);
    }

    public List<AccountLedger> getLedgers() {
        return Collections.unmodifiableList(ledgers);
    }
}
