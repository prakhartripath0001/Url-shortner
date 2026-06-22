package com.shortify.auth_service.common.exception;

import org.springframework.http.HttpStatus;

public class TokenExpiredException extends AuthException {

    public TokenExpiredException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}
