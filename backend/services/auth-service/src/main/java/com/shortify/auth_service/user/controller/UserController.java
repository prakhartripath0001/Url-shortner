package com.shortify.auth_service.user.controller;

import com.shortify.auth_service.common.response.ApiResponse;
import com.shortify.auth_service.user.dto.UserProfileResponse;
import com.shortify.auth_service.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User profile operations")
@SecurityRequirement(name = "BearerAuth")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "Get current user's profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getMyProfile(
            @AuthenticationPrincipal UserDetails userDetails) {
        UserProfileResponse profile = userService.getProfile(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.ok("Profile retrieved", profile));
    }
}
