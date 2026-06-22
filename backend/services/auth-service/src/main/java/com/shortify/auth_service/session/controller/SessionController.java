package com.shortify.auth_service.session.controller;

import com.shortify.auth_service.common.response.ApiResponse;
import com.shortify.auth_service.entity.Session;
import com.shortify.auth_service.entity.User;
import com.shortify.auth_service.security.userdetails.CustomUserDetails;
import com.shortify.auth_service.session.service.SessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
@Tag(name = "Sessions", description = "User session management")
@SecurityRequirement(name = "BearerAuth")
public class SessionController {

    private final SessionService sessionService;

    @GetMapping
    @Operation(summary = "List all active sessions for the current user")
    public ResponseEntity<ApiResponse<List<Session>>> getActiveSessions(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<Session> sessions = sessionService.getActiveSessions(userDetails.getUser());
        return ResponseEntity.ok(ApiResponse.ok("Active sessions", sessions));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Terminate a specific session by ID")
    public ResponseEntity<ApiResponse<Void>> terminateSession(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        User currentUser = userDetails.getUser();
        sessionService.invalidateSessionById(id, currentUser);
        return ResponseEntity.ok(ApiResponse.ok("Session terminated"));
    }
}
