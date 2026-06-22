package com.shortify.auth_service.auth.service;

import com.shortify.auth_service.auth.repository.VerificationTokenRepository;
import com.shortify.auth_service.common.exception.AuthException;
import com.shortify.auth_service.common.exception.TokenExpiredException;
import com.shortify.auth_service.common.util.TokenGenerator;
import com.shortify.auth_service.entity.User;
import com.shortify.auth_service.entity.VerificationToken;
import com.shortify.auth_service.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private static final long VERIFICATION_TOKEN_EXPIRY_HOURS = 24;

    private final VerificationTokenRepository verificationTokenRepository;
    private final UserRepository userRepository;

    @Transactional
    public void sendVerificationEmail(User user) {
        // Remove any existing token for this user before creating a new one
        verificationTokenRepository.deleteByUser(user);

        String tokenValue = TokenGenerator.generateDefault();
        LocalDateTime expiryDate = LocalDateTime.now().plusHours(VERIFICATION_TOKEN_EXPIRY_HOURS);

        VerificationToken token = VerificationToken.builder()
                .user(user)
                .token(tokenValue)
                .expiryDate(expiryDate)
                .build();

        verificationTokenRepository.save(token);

        // In production, inject JavaMailSender here and send a real email.
        // For now, the token is logged so you can verify manually via GET /api/auth/verify-email?token=...
        log.info("[EMAIL VERIFICATION] User: {} | Token: {} | Expires: {}",
                user.getEmail(), tokenValue, expiryDate);
    }

    @Transactional
    public void verifyEmail(String tokenValue) {
        VerificationToken token = verificationTokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new AuthException("Invalid verification token", HttpStatus.BAD_REQUEST));

        if (token.isExpired()) {
            verificationTokenRepository.delete(token);
            throw new TokenExpiredException("Verification token has expired. Please request a new one.");
        }

        User user = token.getUser();
        user.setEnabled(true);
        userRepository.save(user);

        verificationTokenRepository.delete(token);
        log.info("Email verified successfully for user: {}", user.getEmail());
    }

    @Transactional
    public void resendVerification(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException("No account found with email: " + email, HttpStatus.NOT_FOUND));

        if (user.isEnabled()) {
            throw new AuthException("Email is already verified", HttpStatus.CONFLICT);
        }

        sendVerificationEmail(user);
    }
}
