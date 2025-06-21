// src/main/java/com/example/shopmanagement/Config/SecurityConfig.java
package com.example.shopmanagement.Config; // Ensure this package path is correct for your project

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer; // Import for AbstractHttpConfigurer
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays; // Import Arrays for List.of
import java.util.List;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable) // Disable CSRF for API endpoints
            .authorizeHttpRequests(auth -> auth
                // Temporarily permit all requests for easier development.
                // In a production environment, you would secure specific paths
                // with authentication and role-based authorization (e.g., .requestMatchers("/api/admin/**").hasRole("ADMIN"))
                .anyRequest().permitAll()
            )
            .cors(cors -> cors.configurationSource(corsConfigurationSource())); // Apply the CORS configuration

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        // Consolidate all allowed origins for local development
        config.setAllowedOrigins(List.of(
            "http://localhost:8081",
            "http://127.0.0.1:8081",
            "http://localhost:3000",
            "http://127.0.0.1:3000",
            "http://localhost:5173", // Primary Vite/React dev server
            "http://127.0.0.1:5173"  // Alternative for Vite/React dev server
        ));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")); // Added PATCH
        config.setAllowedHeaders(List.of("*")); // Allows all headers
        config.setAllowCredentials(true); // Important if you're using cookies or Authorization headers

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // Apply this CORS config to all paths
        return source;
    }
}
