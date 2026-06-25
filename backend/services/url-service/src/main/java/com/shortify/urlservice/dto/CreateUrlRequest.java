package com.shortify.urlservice.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

import java.time.Instant;

/**
 * Request DTO for URL creation.
 *
 * Validation rules:
 * - originalUrl must be a valid HTTP/HTTPS URL
 * - customAlias if provided: alphanumeric + hyphens, 3-50 chars
 * - expiresAt must be a future date if provided
 */
public record CreateUrlRequest(

        @NotBlank(message = "URL must not be blank")
        @URL(message = "Must be a valid HTTP or HTTPS URL")
        String originalUrl,

        @Size(min = 3, max = 50, message = "Custom alias must be between 3 and 50 characters")
        @Pattern(
            regexp = "^[a-zA-Z0-9\\-_]*$",
            message = "Custom alias can only contain letters, numbers, hyphens, and underscores"
        )
        String customAlias,

        @Size(max = 500, message = "Title must not exceed 500 characters")
        String title,

        @Future(message = "Expiry date must be in the future")
        Instant expiresAt,

        boolean isPrivate
) {}
