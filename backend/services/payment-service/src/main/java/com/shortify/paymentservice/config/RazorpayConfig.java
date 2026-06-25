package com.shortify.paymentservice.config;

import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Razorpay SDK Client Configuration.
 *
 * HOW RAZORPAY AUTHENTICATION WORKS:
 * - Every API call to Razorpay uses HTTP Basic Auth.
 * - Username = Key ID (rzp_test_xxx or rzp_live_xxx)
 * - Password = Key Secret
 *
 * KEY TYPES:
 * - Test keys (rzp_test_*): Use for development/staging. Real money NOT charged.
 * - Live keys (rzp_live_*): Use in production ONLY. Real money IS charged.
 *
 * SECURITY:
 * - NEVER hardcode keys in source code.
 * - NEVER commit keys to Git.
 * - Use environment variables → injected via Docker / Kubernetes secrets.
 *
 * The RazorpayClient is a thread-safe singleton — safe to share across requests.
 */
@Slf4j
@Configuration
public class RazorpayConfig {

    @Value("${razorpay.key-id}")
    private String keyId;

    @Value("${razorpay.key-secret}")
    private String keySecret;

    @Bean
    public RazorpayClient razorpayClient() throws RazorpayException {
        log.info("Initialising Razorpay client — key prefix: {}",
            keyId != null && keyId.length() > 8 ? keyId.substring(0, 8) + "..." : "NOT_SET");

        if (keyId == null || keyId.isBlank() || keySecret == null || keySecret.isBlank()) {
            log.warn("Razorpay credentials not configured. Payment features will be unavailable.");
        }

        return new RazorpayClient(keyId, keySecret);
    }
}
