package com.bankingsystem.information.read.application.model;

public record ApiTrafficItem(
    String clientName,
    String requestId,
    String responseStatus,
    String traceId,
    String requestedAt,
    String latency
) {
}
