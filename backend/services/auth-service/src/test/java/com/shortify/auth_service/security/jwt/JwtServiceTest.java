package com.shortify.auth_service.security.jwt;

import com.shortify.auth_service.config.JwtConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("JwtService Unit Tests")
class JwtServiceTest {

    @Mock
    private JwtConfig jwtConfig;

    @InjectMocks
    private JwtService jwtService;

    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        when(jwtConfig.secret()).thenReturn("test-secret-key-that-is-at-least-32-chars-long!");
        when(jwtConfig.accessTokenExpiration()).thenReturn(900_000L);
        when(jwtConfig.refreshTokenExpiration()).thenReturn(604_800_000L);

        userDetails = User.builder()
                .username("testuser")
                .password("password")
                .authorities(Collections.emptyList())
                .build();
    }

    @Test
    @DisplayName("Should generate a non-null access token")
    void shouldGenerateAccessToken() {
        String token = jwtService.generateAccessToken(userDetails);
        assertThat(token).isNotNull().isNotBlank();
    }

    @Test
    @DisplayName("Should extract correct username from token")
    void shouldExtractUsername() {
        String token = jwtService.generateAccessToken(userDetails);
        String username = jwtService.extractUsername(token);
        assertThat(username).isEqualTo("testuser");
    }

    @Test
    @DisplayName("Should validate a freshly generated token as valid")
    void shouldValidateToken() {
        String token = jwtService.generateAccessToken(userDetails);
        boolean isValid = jwtService.isTokenValid(token, userDetails);
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Should reject a token generated for a different user")
    void shouldRejectTokenForWrongUser() {
        String token = jwtService.generateAccessToken(userDetails);
        UserDetails otherUser = User.builder()
                .username("otheruser")
                .password("password")
                .authorities(Collections.emptyList())
                .build();
        boolean isValid = jwtService.isTokenValid(token, otherUser);
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Should generate distinct refresh token from access token")
    void shouldGenerateRefreshToken() {
        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);
        assertThat(refreshToken).isNotEqualTo(accessToken);
        assertThat(jwtService.extractUsername(refreshToken)).isEqualTo("testuser");
    }
}
