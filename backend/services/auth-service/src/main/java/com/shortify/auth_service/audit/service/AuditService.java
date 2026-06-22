package com.shortify.auth_service.audit.service;

import com.shortify.auth_service.audit.repository.AuditLogRepository;
import com.shortify.auth_service.entity.AuditLog;
import com.shortify.auth_service.entity.User;
import com.shortify.auth_service.enums.AuditAction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    /**
     * Logs an audit event. Can be called with null user for anonymous/system actions.
     */
    @Async
    public void log(User user, AuditAction action, String description, String ipAddress) {
        try {
            AuditLog auditLog = AuditLog.builder()
                    .user(user)
                    .action(action)
                    .description(description)
                    .ipAddress(ipAddress)
                    .build();
            auditLogRepository.save(auditLog);
            log.debug("Audit log recorded: action={}, user={}", action, user != null ? user.getUsername() : "anonymous");
        } catch (Exception ex) {
            log.error("Failed to persist audit log: action={}, error={}", action, ex.getMessage());
        }
    }

    public void log(User user, AuditAction action, String description) {
        log(user, action, description, null);
    }
}
