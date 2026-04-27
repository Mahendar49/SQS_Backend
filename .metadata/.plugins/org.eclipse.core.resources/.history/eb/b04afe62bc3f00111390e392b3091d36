package com.smartqueue.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter corsWebFilter() {

        CorsConfiguration config = new CorsConfiguration();

        // 🔥 Allow frontend origin (IMPORTANT)
        config.setAllowedOrigins(List.of("http://localhost:5173"));

        // 🔥 Allow methods
        config.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "DELETE", "OPTIONS"
        ));

        // 🔥 Allow headers
        config.setAllowedHeaders(List.of(
                "*"
        ));

        // 🔥 Expose headers (optional but useful)
        config.setExposedHeaders(List.of(
                HttpHeaders.AUTHORIZATION
        ));

        // 🔥 Allow credentials (IMPORTANT for JWT)
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", config);

        return new CorsWebFilter(source);
    }
}