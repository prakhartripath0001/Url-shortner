package com.shortify.auth_service.auth.dto;

import com.shortify.auth_service.common.constants.JwtConstants;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthResponse {

    private String accessToken;
    private String refreshToken;

    @Builder.Default
    private String tokenType = JwtConstants.TOKEN_TYPE;

    private long expiresIn;
    private UserResponse user;
}
