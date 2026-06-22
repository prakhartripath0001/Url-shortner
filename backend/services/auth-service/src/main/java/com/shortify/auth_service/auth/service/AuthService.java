package com.shortify.auth_service.auth.service;

import com.shortify.auth_service.audit.service.AuditService;
import com.shortify.auth_service.auth.dto.*;
import com.shortify.auth_service.common.exception.AuthException;
import com.shortify.auth_service.entity.RefreshToken;
import com.shortify.auth_service.entity.User;
import com.shortify.auth_service.enums.AuditAction;
import com.shortify.auth_service.enums.Role;
import com.shortify.auth_service.security.userdetails.CustomUserDetails;
import com.shortify.auth_service.session.service.SessionService;
import com.shortify.auth_service.user.repository.UserRepository;
import com.shortify.auth_service.security.jwt.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final EmailVerificationService emailVerificationService;
    private final SessionService sessionService;
    private final AuditService auditService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new AuthException("Username is already taken", HttpStatus.CONFLICT);
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new AuthException("Email address is already registered", HttpStatus.CONFLICT);
        }

        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.ROLE_USER)
                .build();

        user = userRepository.save(user);
        emailVerificationService.sendVerificationEmail(user);
        auditService.log(user, AuditAction.REGISTRATION_INITIATED,
                "New user registration: " + user.getEmail());

        return UserResponse.from(user);
    }

    @Transactional
    public AuthResponse login(LoginRequest request, HttpServletRequest httpRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.usernameOrEmail(), request.password()
                    )
            );

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            User user = userDetails.getUser();

            String accessToken = jwtService.generateAccessToken(userDetails);
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
            sessionService.createSession(user, httpRequest);

            auditService.log(user, AuditAction.LOGIN_SUCCESS,
                    "Successful login", extractIpAddress(httpRequest));

            return AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken.getToken())
                    .expiresIn(jwtService.getAccessTokenExpiration())
                    .user(UserResponse.from(user))
                    .build();

        } catch (AuthenticationException ex) {
            // Log failure if we can find the user (avoid double lookup on unknown users)
            userRepository.findByUsernameOrEmail(request.usernameOrEmail(), request.usernameOrEmail())
                    .ifPresent(user -> auditService.log(user, AuditAction.LOGIN_FAILURE,
                            "Failed login attempt", extractIpAddress(httpRequest)));
            throw ex;
        }
    }

    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken storedToken = refreshTokenService.validateRefreshToken(request.refreshToken());
        User user = storedToken.getUser();
        CustomUserDetails userDetails = new CustomUserDetails(user);

        // Rotate: revoke old, issue new
        refreshTokenService.revokeToken(request.refreshToken());
        String newAccessToken = jwtService.generateAccessToken(userDetails);
        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken.getToken())
                .expiresIn(jwtService.getAccessTokenExpiration())
                .user(UserResponse.from(user))
                .build();
    }

    @Transactional
    public void logout(String refreshToken, String sessionToken) {
        refreshTokenService.findAndLoadUser(refreshToken).ifPresent(user -> {
            refreshTokenService.revokeToken(refreshToken);
            auditService.log(user, AuditAction.LOGOUT, "User logged out");
        });

        if (sessionToken != null && !sessionToken.isBlank()) {
            sessionService.invalidateSession(sessionToken);
        }
    }

    private String extractIpAddress(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
