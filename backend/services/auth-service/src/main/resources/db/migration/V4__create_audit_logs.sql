CREATE TABLE audit_logs (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            user_id BIGINT NULL,
                            action VARCHAR(50) NOT NULL, -- Maps to AuditAction enum
                            description TEXT NOT NULL,
                            ip_address VARCHAR(45) NULL,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                            CONSTRAINT fk_audit_logs_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
                            INDEX idx_audit_logs_created (created_at) -- Vital for pagination sorting
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;