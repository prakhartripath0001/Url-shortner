CREATE TABLE verification_tokens (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     user_id BIGINT NOT NULL,
                                     token VARCHAR(255) NOT NULL UNIQUE,
                                     expiry_date TIMESTAMP NOT NULL,
                                     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                                     CONSTRAINT fk_verification_tokens_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                                     INDEX idx_verification_tokens_token (token)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;