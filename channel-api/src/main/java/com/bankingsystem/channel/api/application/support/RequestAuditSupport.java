package com.bankingsystem.channel.api.application.support;

import jakarta.servlet.http.HttpServletRequest;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.springframework.util.StringUtils;

public final class RequestAuditSupport {

    private RequestAuditSupport() {
    }

    public static String resolveTraceId(HttpServletRequest request) {
        Object attribute = request.getAttribute(ChannelApiConstants.TRACE_ID_ATTRIBUTE);
        if (attribute instanceof String traceId && StringUtils.hasText(traceId)) {
            return traceId;
        }

        String headerTraceId = request.getHeader(ChannelApiConstants.TRACE_ID_HEADER);
        if (StringUtils.hasText(headerTraceId)) {
            return headerTraceId;
        }

        return "trace-" + UUID.randomUUID();
    }

    public static OffsetDateTime resolveRequestAt(HttpServletRequest request) {
        Object attribute = request.getAttribute(ChannelApiConstants.REQUEST_AT_ATTRIBUTE);
        if (attribute instanceof OffsetDateTime requestAt) {
            return requestAt;
        }
        return OffsetDateTime.now();
    }

    public static void bindTransactionId(HttpServletRequest request, Long transactionId) {
        if (transactionId != null) {
            request.setAttribute(ChannelApiConstants.TRANSACTION_ID_ATTRIBUTE, transactionId);
        }
    }

    public static Long resolveTransactionId(HttpServletRequest request) {
        Object attribute = request.getAttribute(ChannelApiConstants.TRANSACTION_ID_ATTRIBUTE);
        if (attribute instanceof Long transactionId) {
            return transactionId;
        }
        return null;
    }

    public static String resolveRequestId(HttpServletRequest request) {
        String requestId = request.getHeader(ChannelApiConstants.REQUEST_ID_HEADER);
        if (StringUtils.hasText(requestId)) {
            return requestId;
        }
        return "AUTO-" + resolveTraceId(request);
    }

    public static String resolveIdempotencyKey(HttpServletRequest request) {
        String idempotencyKey = request.getHeader(ChannelApiConstants.IDEMPOTENCY_KEY_HEADER);
        return StringUtils.hasText(idempotencyKey) ? idempotencyKey : null;
    }
}
