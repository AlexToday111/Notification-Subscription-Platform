package com.example.notificationplatform.infrastructure.security;

import com.example.notificationplatform.application.audit.AuditService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuditingAccessDeniedHandler implements AccessDeniedHandler {

    private final AuditService auditService;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
            throws IOException, ServletException {
        log.warn("RBAC denied path={} method={}", request.getRequestURI(), request.getMethod());
        auditService.record("RBAC_DENIED", "HttpRequest", request.getRequestURI(), request.getMethod());
        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden");
    }
}
