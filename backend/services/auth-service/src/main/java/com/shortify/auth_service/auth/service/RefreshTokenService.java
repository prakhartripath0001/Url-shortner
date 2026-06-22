package com.shortify.auth_service.auth.service;

import com.shortify.auth_service.auth.repository.RefreshTokenRepository;
import com.shortify.auth_service.common.exception.AuthException;
import com.shortify.auth_service.common.exception.TokenExpiredException;
import com.shortify.auth_service.entity.RefreshToken;
import com.shortify.auth_service.entity.User;
import com.shortify.auth_service.security.jwt.JwtService;
import com.shortify.auth_service.security.userdetails.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;

    @Transactional
    public RefreshToken createRefreshToken(User user) {
        // Generate a JWT-based refresh token and store it for validation
        CustomUserDetails userDetails = new CustomUserDetails(user);
        String tokenValue = jwtService.generateRefreshToken(userDetails);

        long refreshExpirationMs = jwtService.getAccessTokenExpiration() * 8; // 8x access token life
        LocalDateTime expiryDate = LocalDateTime.now().plusNanos(refreshExpirationMs * 1_000_000L);

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(tokenValue)
                .expiryDate(expiryDate)
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    @Transactional(readOnly = true)
    public RefreshToken validateRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new AuthException("Refresh token not found or already used", HttpStatus.UNAUTHORIZED));

        if (refreshToken.isRevoked()) {
            throw new AuthException("Refresh token has been revoked", HttpStatus.UNAUTHORIZED);
        }

        if (refreshToken.isExpired()) {
            throw new TokenExpiredException("Refresh token has expired. Please log in again.");
        }

        return refreshToken;
    }

    @Transactional
    public void revokeToken(String token) {
        refreshTokenRepository.findByToken(token).ifPresent(rt -> {
            rt.setRevokedAt(LocalDateTime.now());
            refreshTokenRepository.save(rt);
        });
    }

    @Transactional
    public void revokeAllForUser(User user) {
        refreshTokenRepository.revokeAllActiveTokensByUser(user);
    }

    @Transactional(readOnly = true)
    public java.util.Optional<User> findAndLoadUser(String token) {
        return refreshTokenRepository.findByToken(token).map(RefreshToken::getUser);
    }
}
