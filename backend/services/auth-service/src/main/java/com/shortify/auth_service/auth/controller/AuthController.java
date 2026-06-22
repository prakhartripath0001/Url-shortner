package com.shortify.auth_service.auth.controller;

import com.shortify.auth_service.auth.dto.*;
import com.shortify.auth_service.auth.service.AuthService;
import com.shortify.auth_service.auth.service.EmailVerificationService;
import com.shortify.auth_service.auth.service.PasswordResetService;
import com.shortify.auth_service.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Authentication and account management endpoints")
public class AuthController {

    private final AuthService authService;
    private final EmailVerificationService emailVerificationService;
    private final PasswordResetService passwordResetService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user account")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody RegisterRequest request) {
        UserResponse user = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Registration successful. Please verify your email.", user));
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate and receive access + refresh tokens")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {
        AuthResponse auth = authService.login(request, httpRequest);
        return ResponseEntity.ok(ApiResponse.ok("Login successful", auth));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Exchange a valid refresh token for a new access token")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(
            @Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse auth = authService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.ok("Token refreshed", auth));
    }

    @PostMapping("/logout")
    @Operation(summary = "Revoke refresh token and invalidate the current session")
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestParam(required = false) String refreshToken,
            @RequestParam(required = false) String sessionToken) {
        authService.logout(refreshToken, sessionToken);
        return ResponseEntity.ok(ApiResponse.ok("Logged out successfully"));
    }

    @GetMapping("/verify-email")
    @Operation(summary = "Verify email address using the token sent during registration")
    public ResponseEntity<ApiResponse<Void>> verifyEmail(@RequestParam String token) {
        emailVerificationService.verifyEmail(token);
        return ResponseEntity.ok(ApiResponse.ok("Email verified successfully. You can now log in."));
    }

    @PostMapping("/resend-verification")
    @Operation(summary = "Resend email verification link")
    public ResponseEntity<ApiResponse<Void>> resendVerification(@RequestParam String email) {
        emailVerificationService.resendVerification(email);
        return ResponseEntity.ok(ApiResponse.ok("Verification email sent. Please check your inbox."));
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Request a password reset email")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {
        passwordResetService.requestPasswordReset(request.email());
        // Always return 200 to avoid email enumeration
        return ResponseEntity.ok(ApiResponse.ok("If that email exists, a reset link has been sent."));
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset password using the token from the reset email")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
        passwordResetService.resetPassword(request.token(), request.newPassword());
        return ResponseEntity.ok(ApiResponse.ok("Password reset successfully. Please log in."));
    }
}
