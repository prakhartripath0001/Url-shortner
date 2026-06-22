package com.shortify.auth_service.auth.repository;

import com.shortify.auth_service.entity.RefreshToken;
import com.shortify.auth_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    List<RefreshToken> findAllByUserAndRevokedAtIsNull(User user);

    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.revokedAt = CURRENT_TIMESTAMP WHERE rt.user = :user AND rt.revokedAt IS NULL")
    void revokeAllActiveTokensByUser(User user);

    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.user = :user")
    void deleteByUser(User user);
}
