package com.bankingsystem.channel.api.presentation.interceptor;

import com.bankingsystem.channel.api.application.command.ApiRequestLogCommand;
import com.bankingsystem.channel.api.application.service.ApiRequestLogService;
import com.bankingsystem.channel.api.application.support.ChannelApiConstants;
import com.bankingsystem.channel.api.application.support.RequestAuditSupport;
import com.bankingsystem.channel.security.application.model.ResolvedChannelIdentity;
import com.bankingsystem.channel.security.application.service.ChannelIdentityResolver;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.OffsetDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class ApiRequestLoggingInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(ApiRequestLoggingInterceptor.class);

    private final ApiRequestLogService apiRequestLogService;
    private final ChannelIdentityResolver channelIdentityResolver;

    public ApiRequestLoggingInterceptor(
        ApiRequestLogService apiRequestLogService,
        ChannelIdentityResolver channelIdentityResolver
    ) {
        this.apiRequestLogService = apiRequestLogService;
        this.channelIdentityResolver = channelIdentityResolver;
    }

    @Override
    public void afterCompletion(
        HttpServletRequest request,
        HttpServletResponse response,
        Object handler,
        Exception ex
    ) {
        ResolvedChannelIdentity resolvedChannelIdentity = channelIdentityResolver.resolve(
            ChannelApiConstants.OPEN_API_CHANNEL_CODE,
            request.getHeader(ChannelApiConstants.API_CLIENT_KEY_HEADER)
        );

        ApiRequestLogCommand command = new ApiRequestLogCommand(
            resolvedChannelIdentity.apiClientId(),
            resolvedChannelIdentity.channelId(),
            RequestAuditSupport.resolveTransactionId(request),
            RequestAuditSupport.resolveRequestId(request),
            RequestAuditSupport.resolveIdempotencyKey(request),
            RequestAuditSupport.resolveTraceId(request),
            request.getRequestURI(),
            request.getMethod(),
            response.getStatus(),
            RequestAuditSupport.resolveRequestAt(request),
            OffsetDateTime.now()
        );

        try {
            apiRequestLogService.save(command);
        } catch (Exception loggingException) {
            log.warn(
                "API 요청 이력 저장에 실패했습니다. traceId={}, path={}",
                command.traceId(),
                command.apiPath(),
                loggingException
            );
        }
    }
}
