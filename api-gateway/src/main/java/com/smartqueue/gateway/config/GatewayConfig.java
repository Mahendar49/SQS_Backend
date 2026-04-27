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
                .route("auth-service", r -> r
                        .path("/api/v1/auth/**")
                        .uri("http://localhost:8081"))

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