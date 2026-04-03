package com.bankingsystem.information.read.application.model;

public record TransactionTimelineItem(
    String transactionType,
    String transactionId,
    String summary,
    String occurredAt,
    String status
) {
}
