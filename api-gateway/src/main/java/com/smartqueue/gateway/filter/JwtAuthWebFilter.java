package com.smartqueue.gateway.filter;

import java.util.List;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.smartqueue.gateway.config.RouteSecurityConfig;
import com.smartqueue.gateway.util.JwtUtil;

import io.jsonwebtoken.Claims;
import reactor.core.publisher.Mono;

/**
 * Purpose:
 * JWT Authentication + Authorization Filter
 */
@Component
public class JwtAuthWebFilter implements GatewayFilter {

    private final JwtUtil jwtUtil;
    private final RouteSecurityConfig securityConfig;

    public JwtAuthWebFilter(JwtUtil jwtUtil, RouteSecurityConfig securityConfig) {
        this.jwtUtil = jwtUtil;
        this.securityConfig = securityConfig;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();

        // 🔓 Skip auth routes
        if (path.startsWith("/api/v1/auth")) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return onError(exchange, "Missing Authorization Header", HttpStatus.UNAUTHORIZED);
        }

        String token = authHeader.substring(7);

        try {
            Claims claims = jwtUtil.validateAndExtract(token);

            String userId = claims.getSubject();
            List<String> roles = claims.get("roles", List.class);

            // ================= ROLE CHECK =================
            var allowedRolesOpt = securityConfig.getAllowedRoles(path);

            if (allowedRolesOpt.isPresent()) {
                boolean hasRole = roles.stream()
                        .anyMatch(allowedRolesOpt.get()::contains);

                if (!hasRole) {
                    return onError(exchange, "Access Denied - Role", HttpStatus.FORBIDDEN);
                }
            }

            // ================= SAFE HEADER INJECTION =================
            ServerWebExchange mutatedExchange = exchange.mutate()
                    .request(builder -> builder
                            .header("X-User-Id", userId)
                            .header("X-User-Roles", roles.toString())
                    )
                    .build();

            return chain.filter(mutatedExchange);

        } catch (Exception e) {
            return onError(exchange, "Invalid Token", HttpStatus.UNAUTHORIZED);
        }
    }

    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus status) {
        exchange.getResponse().setStatusCode(status);
        return exchange.getResponse().setComplete();
    }
}