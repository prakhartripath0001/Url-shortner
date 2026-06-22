package com.shortify.auth_service.common.util;

import java.security.SecureRandom;
import java.util.Base64;

public final class TokenGenerator {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private TokenGenerator() {}

    /**
     * Generates a cryptographically secure URL-safe random token.
     *
     * @param byteLength number of random bytes (token length ≈ byteLength * 1.33 in Base64)
     * @return URL-safe Base64-encoded token string
     */
    public static String generate(int byteLength) {
        byte[] bytes = new byte[byteLength];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    /**
     * Generates a default 32-byte (43 character) token suitable for email verification
     * and password reset flows.
     */
    public static String generateDefault() {
        return generate(32);
    }
}
