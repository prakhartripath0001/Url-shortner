package com.shortify.auth_service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public record JwtConfig(
        String secret,
        long accessTokenExpiration,
        long refreshTokenExpiration
) {}
