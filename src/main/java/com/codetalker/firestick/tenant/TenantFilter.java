package com.codetalker.firestick.tenant;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Populates {@link TenantContext} for each request. It looks for an "app" query parameter first,
 * then the "X-App" header. Falls back to the default tenant if neither is provided.
 */
@Component
@Order(10)
public class TenantFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(TenantFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String app = request.getParameter("app");
        if (app == null || app.isBlank()) {
            app = request.getHeader("X-App");
        }
        if (app == null || app.isBlank()) {
            app = TenantContext.DEFAULT_TENANT;
        }
        TenantContext.set(app);
        try {
            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }
}
