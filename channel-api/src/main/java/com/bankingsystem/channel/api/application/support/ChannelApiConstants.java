package com.bankingsystem.channel.api.application.support;

public final class ChannelApiConstants {

    public static final String REQUEST_ID_HEADER = "X-Request-Id";
    public static final String IDEMPOTENCY_KEY_HEADER = "Idempotency-Key";
    public static final String TRACE_ID_HEADER = "X-Trace-Id";
    public static final String API_CLIENT_KEY_HEADER = "X-Api-Client-Key";

    public static final String TRACE_ID_ATTRIBUTE = "channel.api.traceId";
    public static final String REQUEST_AT_ATTRIBUTE = "channel.api.requestAt";
    public static final String TRANSACTION_ID_ATTRIBUTE = "channel.api.transactionId";
    public static final String OPEN_API_CHANNEL_CODE = "OPEN_API";

    private ChannelApiConstants() {
    }
}
