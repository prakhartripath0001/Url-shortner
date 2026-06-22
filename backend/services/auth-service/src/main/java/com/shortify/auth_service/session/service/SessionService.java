package com.shortify.auth_service.session.service;

import com.shortify.auth_service.common.exception.AuthException;
import com.shortify.auth_service.common.util.TokenGenerator;
import com.shortify.auth_service.entity.Session;
import com.shortify.auth_service.entity.User;
import com.shortify.auth_service.session.repository.SessionRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;

    @Transactional
    public Session createSession(User user, HttpServletRequest request) {
        String ipAddress = extractIpAddress(request);
        String userAgent = request.getHeader("User-Agent");
        String sessionToken = TokenGenerator.generateDefault();

        Session session = Session.builder()
                .user(user)
                .sessionToken(sessionToken)
                .ipAddress(ipAddress != null ? ipAddress : "unknown")
                .userAgent(userAgent)
                .isActive(true)
                .build();

        return sessionRepository.save(session);
    }

    @Transactional
    public void invalidateSession(String sessionToken) {
        sessionRepository.findBySessionToken(sessionToken).ifPresent(session -> {
            session.setActive(false);
            sessionRepository.save(session);
        });
    }

    @Transactional
    public void invalidateSessionById(Long sessionId, User currentUser) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new AuthException("Session not found", HttpStatus.NOT_FOUND));

        if (!session.getUser().getId().equals(currentUser.getId())) {
            throw new AuthException("Cannot terminate another user's session", HttpStatus.FORBIDDEN);
        }

        session.setActive(false);
        sessionRepository.save(session);
    }

    @Transactional(readOnly = true)
    public List<Session> getActiveSessions(User user) {
        return sessionRepository.findByUserAndIsActive(user, true);
    }

    private String extractIpAddress(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
