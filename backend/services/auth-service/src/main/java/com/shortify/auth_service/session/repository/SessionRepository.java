package com.shortify.auth_service.session.repository;

import com.shortify.auth_service.entity.Session;
import com.shortify.auth_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {

    /**
     * Leverages composite index (user_id, is_active) for fast session lookups.
     */
    List<Session> findByUserAndIsActive(User user, boolean isActive);

    Optional<Session> findBySessionTokenAndIsActive(String sessionToken, boolean isActive);

    Optional<Session> findBySessionToken(String sessionToken);
}
