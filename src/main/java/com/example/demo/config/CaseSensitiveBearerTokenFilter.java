package com.example.demo.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter to enforce case-sensitive "Bearer" prefix in Authorization header.
 * Rejects requests with lowercase "bearer" or other case variations.
 */
@Component
public class CaseSensitiveBearerTokenFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                   HttpServletResponse response, 
                                   FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        
        if (authHeader != null && authHeader.startsWith("bearer ")) {
            // Reject lowercase "bearer" - must be "Bearer" (case-sensitive)
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Invalid Authorization header format. Use 'Bearer' (capital B)\"}");
            return;
        }
        
        filterChain.doFilter(request, response);
    }
}
