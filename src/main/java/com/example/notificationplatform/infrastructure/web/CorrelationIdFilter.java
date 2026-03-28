package com.example.notificationplatform.infrastructure.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class CorrelationIdFilter extends OncePerRequestFilter {
    public static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
    public static final String REQUEST_ID_HEADER = "X-Request-Id";
    public static final String CORRELATION_ID = "correlationId";
    public static final String REQUEST_ID = "requestId";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String correlationId = headerOrNew(request, CORRELATION_ID_HEADER);
        String requestId = headerOrNew(request, REQUEST_ID_HEADER);
        MDC.put(CORRELATION_ID, correlationId);
        MDC.put(REQUEST_ID, requestId);
        response.setHeader(CORRELATION_ID_HEADER, correlationId);
        response.setHeader(REQUEST_ID_HEADER, requestId);
        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(CORRELATION_ID);
            MDC.remove(REQUEST_ID);
        }
    }

    private String headerOrNew(HttpServletRequest request, String header) {
        String value = request.getHeader(header);
        return value == null || value.isBlank() ? UUID.randomUUID().toString() : value.trim();
    }
}
