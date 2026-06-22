package com.shortify.auth_service.auth.repository;

import com.shortify.auth_service.entity.User;
import com.shortify.auth_service.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    Optional<VerificationToken> findByToken(String token);

    @Modifying
    @Query("DELETE FROM VerificationToken vt WHERE vt.user = :user")
    void deleteByUser(User user);
}
