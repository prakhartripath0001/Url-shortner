package com.shortify.urlservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class AliasAlreadyTakenException extends RuntimeException {
    public AliasAlreadyTakenException(String message) {
        super(message);
    }
}
