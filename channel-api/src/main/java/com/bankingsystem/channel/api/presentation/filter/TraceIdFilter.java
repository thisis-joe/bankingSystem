package com.bankingsystem.channel.api.presentation.filter;

import com.bankingsystem.channel.api.application.support.ChannelApiConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class TraceIdFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        String traceId = request.getHeader(ChannelApiConstants.TRACE_ID_HEADER);
        if (!StringUtils.hasText(traceId)) {
            traceId = "trace-" + UUID.randomUUID();
        }

        request.setAttribute(ChannelApiConstants.TRACE_ID_ATTRIBUTE, traceId);
        request.setAttribute(ChannelApiConstants.REQUEST_AT_ATTRIBUTE, OffsetDateTime.now());
        response.setHeader(ChannelApiConstants.TRACE_ID_HEADER, traceId);

        MDC.put("traceId", traceId);
        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove("traceId");
        }
    }
}
