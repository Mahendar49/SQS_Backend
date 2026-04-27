package com.smartqueue.gateway.config;

import com.smartqueue.gateway.filter.JwtAuthWebFilter;
import org.springframework.cloud.gateway.route.*;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.*;

@Configuration
public class GatewayConfig {

    private final JwtAuthWebFilter jwtFilter;

    public GatewayConfig(JwtAuthWebFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public RouteLocator customRoutes(RouteLocatorBuilder builder) {

        return builder.routes()

                // 🔓 PUBLIC ROUTES
                .route("auth-service-public", r -> r
                        .path(
                                "/api/v1/auth/register/**",
                                "/api/v1/auth/login",
                                "/api/v1/auth/verify",
                                "/api/v1/auth/resend-otp",
                                "/api/v1/auth/refresh"
                        )
                        .uri("http://localhost:8081"))

                // 🔐 AUTH PROTECTED ROUTES
                .route("auth-service-protected", r -> r
                        .path("/api/v1/auth/logout")
                        .filters(f -> f.filter(jwtFilter))
                        .uri("http://localhost:8081"))

                // 🔓 PUBLIC BUSINESS ROUTES
                .route("business-public-service", r -> r
                        .path("/api/v1/public/business/**")
                        .uri("http://localhost:8082"))

                // 🔐 BUSINESS SERVICE (JWT PROTECTED)
                .route("business-service", r -> r
                        .path("/api/v1/admin/business/**")
                        .filters(f -> f.filter(jwtFilter))
                        .uri("http://localhost:8082"))

                // 🔐 QUEUE SERVICE (JWT PROTECTED)
                .route("queue-service", r -> r
                        .path("/api/v1/queue/**")
                        .filters(f -> f.filter(jwtFilter))
                        .uri("http://localhost:8083"))

                .build();
    }
}
