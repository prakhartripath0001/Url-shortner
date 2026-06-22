package com.shortify.auth_service.security.jwt;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtClaimsExtractor {

    private final JwtService jwtService;

    public String extractUsername(String token) {
        return jwtService.extractUsername(token);
    }

    public Date extractExpiration(String token) {
        return jwtService.extractExpiration(token);
    }

    public <T> T extractClaim(String token, java.util.function.Function<Claims, T> claimsResolver) {
        return jwtService.extractClaim(token, claimsResolver);
    }

    public String extractRole(String token) {
        return jwtService.extractClaim(token, claims -> claims.get("role", String.class));
    }
}
