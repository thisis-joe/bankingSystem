package com.bankingsystem.channel.api.presentation.response;

import com.bankingsystem.transfer.application.result.TransferResult;
import java.time.LocalDate;
import java.time.OffsetDateTime;

public record TransferResponse(
    Long transactionId,
    String transactionType,
    String transactionStatus,
    String requestId,
    String idempotencyKey,
    LocalDate businessDate,
    OffsetDateTime occurredAt,
    OffsetDateTime postedAt,
    String description,
    String traceId
) {

    public static TransferResponse from(TransferResult result, String traceId) {
        return new TransferResponse(
            result.transactionId(),
            result.transactionType().name(),
            result.transactionStatus().name(),
            result.requestId(),
            result.idempotencyKey(),
            result.businessDate(),
            result.occurredAt(),
            result.postedAt(),
            result.description(),
            traceId
        );
    }
}
