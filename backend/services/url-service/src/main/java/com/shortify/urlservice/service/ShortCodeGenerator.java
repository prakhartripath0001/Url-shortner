package com.shortify.urlservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

/**
 * Generates unique 7-character Base62 short codes.
 *
 * WHY BASE62?
 * - Base64 uses '+' and '/' which are URL-unsafe characters.
 * - Base62 = [a-z, A-Z, 0-9] = 62 chars — all URL-safe.
 * - 62^7 = 3.52 Trillion unique codes (9,589 years at 1M URLs/day).
 *
 * WHY SecureRandom OVER Random?
 * - java.util.Random is predictable — attackers could guess next codes.
 * - SecureRandom uses OS-level entropy — cryptographically unpredictable.
 * - Performance: SecureRandom is slightly slower but acceptable at our scale.
 *
 * COLLISION HANDLING STRATEGY:
 * - On collision (rare), retry with a new random code (up to MAX_RETRIES).
 * - At 1M URLs/day on 3.52T capacity: collision probability ≈ 0.03% per generation.
 * - A distributed ID generator (Snowflake) would eliminate collisions entirely,
 *   but adds operational complexity — collision retry is simpler at this scale.
 */
@Slf4j
@Component
public class ShortCodeGenerator {

    private static final String BASE62 = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 7;
    private static final int MAX_RETRIES = 5;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    /**
     * Generates a random 7-character Base62 code.
     */
    public String generate() {
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(BASE62.charAt(SECURE_RANDOM.nextInt(BASE62.length())));
        }
        return sb.toString();
    }

    /**
     * Generates a unique code, checking against the existsChecker for collisions.
     * Retries up to MAX_RETRIES times before throwing.
     *
     * @param existsChecker predicate that returns true if code is already taken
     */
    public String generateUnique(java.util.function.Predicate<String> existsChecker) {
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            String code = generate();
            if (!existsChecker.test(code)) {
                return code;
            }
            log.warn("Short code collision on attempt {}: {}", attempt, code);
        }
        throw new IllegalStateException(
            "Failed to generate unique short code after " + MAX_RETRIES + " attempts. " +
            "This is extremely unlikely — investigate for systematic issues."
        );
    }
}
