package com.shortify.urlservice.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Global exception handler using RFC 7807 Problem Details format.
 *
 * WHY RFC 7807 (ProblemDetail)?
 * - Industry standard for REST API error responses.
 * - Supported natively by Spring 6+ via ProblemDetail.
 * - Structured format allows clients to parse errors programmatically.
 * - Includes type (URI), title, status, detail, and instance fields.
 *
 * Example response:
 * {
 *   "type": "https://shortify.com/errors/url-not-found",
 *   "title": "URL Not Found",
 *   "status": 404,
 *   "detail": "Short URL not found: abc1234",
 *   "instance": "/abc1234"
 * }
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UrlNotFoundException.class)
    public ProblemDetail handleUrlNotFound(UrlNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("URL Not Found");
        problem.setType(URI.create("https://shortify.com/errors/url-not-found"));
        return problem;
    }

    @ExceptionHandler(UrlExpiredException.class)
    public ProblemDetail handleUrlExpired(UrlExpiredException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.GONE, ex.getMessage());
        problem.setTitle("URL Expired");
        problem.setType(URI.create("https://shortify.com/errors/url-expired"));
        return problem;
    }

    @ExceptionHandler(AliasAlreadyTakenException.class)
    public ProblemDetail handleAliasConflict(AliasAlreadyTakenException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Alias Already Taken");
        problem.setType(URI.create("https://shortify.com/errors/alias-taken"));
        return problem;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        e -> e.getField(),
                        e -> e.getDefaultMessage() != null ? e.getDefaultMessage() : "Invalid value"
                ));

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "Request validation failed"
        );
        problem.setTitle("Validation Error");
        problem.setType(URI.create("https://shortify.com/errors/validation"));
        problem.setProperty("errors", errors);
        return problem;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception ex) {
        log.error("Unhandled exception", ex);
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred"
        );
        problem.setTitle("Internal Server Error");
        problem.setType(URI.create("https://shortify.com/errors/internal"));
        return problem;
    }
}
