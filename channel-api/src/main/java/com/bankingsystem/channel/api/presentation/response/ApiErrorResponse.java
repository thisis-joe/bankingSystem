package com.bankingsystem.channel.api.presentation.response;

import java.time.OffsetDateTime;

public record ApiErrorResponse(
    String code,
    String message,
    String path,
    OffsetDateTime timestamp,
    String traceId
) {
}
