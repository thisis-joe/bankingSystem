package com.bankingsystem.channel.api.infrastructure.persistence;

import com.bankingsystem.channel.api.application.command.ApiRequestLogCommand;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;

@Entity
@Table(name = "api_request_log")
public class ApiRequestLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "api_request_log_id")
    private Long apiRequestLogId;

    @Column(name = "api_client_id")
    private Long apiClientId;

    @Column(name = "channel_id", nullable = false)
    private Long channelId;

    @Column(name = "transaction_id")
    private Long transactionId;

    @Column(name = "request_id", nullable = false, length = 100)
    private String requestId;

    @Column(name = "idempotency_key", length = 100)
    private String idempotencyKey;

    @Column(name = "trace_id", nullable = false, length = 100)
    private String traceId;

    @Column(name = "api_path", nullable = false, length = 255)
    private String apiPath;

    @Column(name = "http_method", nullable = false, length = 10)
    private String httpMethod;

    @Column(name = "response_status")
    private Integer responseStatus;

    @Column(name = "request_at", nullable = false)
    private OffsetDateTime requestAt;

    @Column(name = "response_at")
    private OffsetDateTime responseAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    protected ApiRequestLog() {
    }

    private ApiRequestLog(
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
        this.apiClientId = apiClientId;
        this.channelId = channelId;
        this.transactionId = transactionId;
        this.requestId = requestId;
        this.idempotencyKey = idempotencyKey;
        this.traceId = traceId;
        this.apiPath = apiPath;
        this.httpMethod = httpMethod;
        this.responseStatus = responseStatus;
        this.requestAt = requestAt;
        this.responseAt = responseAt;
    }

    public static ApiRequestLog from(ApiRequestLogCommand command) {
        return new ApiRequestLog(
            command.apiClientId(),
            command.channelId(),
            command.transactionId(),
            command.requestId(),
            command.idempotencyKey(),
            command.traceId(),
            command.apiPath(),
            command.httpMethod(),
            command.responseStatus(),
            command.requestAt(),
            command.responseAt()
        );
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = OffsetDateTime.now();
        }
    }
}
