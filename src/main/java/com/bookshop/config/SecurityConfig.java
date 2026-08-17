package com.bookshop.config;

import com.bookshop.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
                // JWT authentication doesn't need CSRF protection
                .csrf(csrf -> csrf.disable())

                .cors(cors -> {})

                // Don't create HTTP sessions
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                // Configure API authorization
                .authorizeHttpRequests(auth -> auth

                        // =========================
                        // PUBLIC APIs
                        // =========================

                        .requestMatchers(
                                "/api/auth/register",
                                "/api/auth/verify-otp",
                                "/api/auth/login"
                        ).permitAll()

                        // =========================
                        // SHOP KEEPER APIs
                        // =========================

                        .requestMatchers(
                                "/api/shopkeeper/**"
                        ).hasRole("SHOP_KEEPER")

                        // =========================
                        // CUSTOMER APIs
                        // =========================

                        .requestMatchers(
                                "/api/customer/**"
                        ).hasRole("CUSTOMER")

                        // =========================
                        // BOOK VIEW APIs
                        // =========================

                        .requestMatchers(
                                "/api/books/**"
                        ).permitAll()

                        // =========================
                        // EVERYTHING ELSE
                        // =========================

                        .anyRequest().authenticated()
                )

                // Add JWT filter before Spring's username/password filter
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    /**
     * AuthenticationManager is used during login.
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {

        return configuration.getAuthenticationManager();
    }

    /**
     * BCrypt password encoder.
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }
}