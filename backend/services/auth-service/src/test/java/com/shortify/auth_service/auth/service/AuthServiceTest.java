package com.shortify.auth_service.auth.service;

import com.shortify.auth_service.audit.service.AuditService;
import com.shortify.auth_service.auth.dto.LoginRequest;
import com.shortify.auth_service.auth.dto.RegisterRequest;
import com.shortify.auth_service.auth.dto.UserResponse;
import com.shortify.auth_service.auth.dto.AuthResponse;
import com.shortify.auth_service.common.exception.AuthException;
import com.shortify.auth_service.entity.RefreshToken;
import com.shortify.auth_service.entity.User;
import com.shortify.auth_service.enums.Role;
import com.shortify.auth_service.security.jwt.JwtService;
import com.shortify.auth_service.security.userdetails.CustomUserDetails;
import com.shortify.auth_service.session.service.SessionService;
import com.shortify.auth_service.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Unit Tests")
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;
    @Mock private RefreshTokenService refreshTokenService;
    @Mock private EmailVerificationService emailVerificationService;
    @Mock private SessionService sessionService;
    @Mock private AuditService auditService;
    @Mock private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("hashed_password")
                .role(Role.ROLE_USER)
                .build();
    }

    @Test
    @DisplayName("Should register a new user successfully")
    void shouldRegisterNewUser() {
        RegisterRequest request = new RegisterRequest("testuser", "test@example.com", "password123");

        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashed_password");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        UserResponse response = authService.register(request);

        assertThat(response).isNotNull();
        assertThat(response.username()).isEqualTo("testuser");
        verify(emailVerificationService).sendVerificationEmail(any(User.class));
        verify(auditService).log(any(), any(), anyString());
    }

    @Test
    @DisplayName("Should throw conflict when username already exists")
    void shouldRejectDuplicateUsername() {
        RegisterRequest request = new RegisterRequest("testuser", "new@example.com", "password123");
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(AuthException.class)
                .hasMessageContaining("Username is already taken");
    }

    @Test
    @DisplayName("Should throw conflict when email already exists")
    void shouldRejectDuplicateEmail() {
        RegisterRequest request = new RegisterRequest("newuser", "test@example.com", "password123");
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(AuthException.class)
                .hasMessageContaining("Email address is already registered");
    }

    @Test
    @DisplayName("Should return AuthResponse on successful login")
    void shouldLoginSuccessfully() {
        LoginRequest request = new LoginRequest("testuser", "password123");
        HttpServletRequest httpRequest = mock(HttpServletRequest.class);
        CustomUserDetails userDetails = new CustomUserDetails(mockUser);
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        RefreshToken mockRefreshToken = RefreshToken.builder()
                .user(mockUser)
                .token("refresh-token-value")
                .expiryDate(LocalDateTime.now().plusDays(7))
                .build();

        when(authenticationManager.authenticate(any())).thenReturn(authToken);
        when(jwtService.generateAccessToken(any())).thenReturn("access-token-value");
        when(jwtService.getAccessTokenExpiration()).thenReturn(900_000L);
        when(refreshTokenService.createRefreshToken(any())).thenReturn(mockRefreshToken);
        when(httpRequest.getHeader(anyString())).thenReturn(null);
        when(httpRequest.getRemoteAddr()).thenReturn("127.0.0.1");

        AuthResponse response = authService.login(request, httpRequest);

        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("access-token-value");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token-value");
    }
}
