package com.bankingsystem.channel.api.application.command;

import java.time.OffsetDateTime;

public record ApiRequestLogCommand(
    Long apiClientId,
    Long channelId,
    Long transactionId,
    String requestId,
    String idempotencyKey,
    String traceId,
    String apiPath,
    String httpMethod,
    Integer responseStatus,
    OffsetDateTime requestAt,
    OffsetDateTime responseAt
) {
}
