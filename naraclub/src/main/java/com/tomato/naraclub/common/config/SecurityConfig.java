package com.tomato.naraclub.common.config;

import com.tomato.naraclub.application.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }
}
