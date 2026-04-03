package com.bankingsystem.information.read.application.model;

public record OperationalLogItem(
    String level,
    String category,
    String message,
    String occurredAt,
    String traceId
) {
}
