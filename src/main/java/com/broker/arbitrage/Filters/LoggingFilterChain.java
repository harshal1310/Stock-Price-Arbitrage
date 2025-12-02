package com.broker.arbitrage.Filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class LoggingFilterChain extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(LoggingFilterChain.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        long start = System.currentTimeMillis();

        String uri = request.getRequestURI();
        String method = request.getMethod();
        String clientIp = request.getRemoteAddr();
        Object auth = SecurityContextHolder.getContext().getAuthentication();

        logger.info(" Request Started: {} {} from {}", method, uri, clientIp);
        logger.info("   User Info: {}", (auth != null ? auth.getClass() : "Anonymous"));

        filterChain.doFilter(request, response);

        long end = System.currentTimeMillis();
        long duration = end - start;

        logger.info("⬅️ Request Ended: {} {} | Duration: {} ms", method, uri, duration);
    }
}
