package com.shortify.auth_service.auth.service;

import com.shortify.auth_service.auth.repository.PasswordResetTokenRepository;
import com.shortify.auth_service.common.exception.AuthException;
import com.shortify.auth_service.common.exception.TokenExpiredException;
import com.shortify.auth_service.common.util.TokenGenerator;
import com.shortify.auth_service.entity.PasswordResetToken;
import com.shortify.auth_service.entity.User;
import com.shortify.auth_service.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private static final long RESET_TOKEN_EXPIRY_HOURS = 1;

    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void requestPasswordReset(String email) {
        // Avoid leaking user existence: always respond with 200 even if email not found
        userRepository.findByEmail(email).ifPresent(user -> {
            passwordResetTokenRepository.deleteByUser(user);

            String tokenValue = TokenGenerator.generateDefault();
            LocalDateTime expiryDate = LocalDateTime.now().plusHours(RESET_TOKEN_EXPIRY_HOURS);

            PasswordResetToken token = PasswordResetToken.builder()
                    .user(user)
                    .token(tokenValue)
                    .expiryDate(expiryDate)
                    .build();

            passwordResetTokenRepository.save(token);

            // In production, send email with link containing the token.
            log.info("[PASSWORD RESET] User: {} | Token: {} | Expires: {}",
                    email, tokenValue, expiryDate);
        });
    }

    @Transactional
    public void resetPassword(String tokenValue, String newPassword) {
        PasswordResetToken token = passwordResetTokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new AuthException("Invalid or already-used reset token", HttpStatus.BAD_REQUEST));

        if (token.isExpired()) {
            passwordResetTokenRepository.delete(token);
            throw new TokenExpiredException("Password reset token has expired. Please request a new one.");
        }

        User user = token.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        passwordResetTokenRepository.delete(token);
        log.info("Password reset successfully for user: {}", user.getEmail());
    }
}
