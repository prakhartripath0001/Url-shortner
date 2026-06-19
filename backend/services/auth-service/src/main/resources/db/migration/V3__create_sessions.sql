CREATE TABLE sessions (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          user_id BIGINT NOT NULL,
                          session_token VARCHAR(255) NOT NULL UNIQUE,
                          ip_address VARCHAR(45) NOT NULL, -- Accommodates both IPv4 and IPv6
                          user_agent VARCHAR(512) NULL,
                          is_active BOOLEAN NOT NULL DEFAULT TRUE,
                          last_accessed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                          CONSTRAINT fk_sessions_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                          INDEX idx_sessions_user_active (user_id, is_active) -- Composite index for quick active session checks
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;