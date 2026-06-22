package com.shortify.auth_service.audit.repository;

import com.shortify.auth_service.entity.AuditLog;
import com.shortify.auth_service.entity.User;
import com.shortify.auth_service.enums.AuditAction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findByUser(User user);

    Page<AuditLog> findByUser(User user, Pageable pageable);

    Page<AuditLog> findByAction(AuditAction action, Pageable pageable);
}
