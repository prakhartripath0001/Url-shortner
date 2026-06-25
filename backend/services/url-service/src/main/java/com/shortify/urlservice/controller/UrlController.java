package com.shortify.urlservice.controller;

import com.shortify.urlservice.dto.CreateUrlRequest;
import com.shortify.urlservice.dto.UrlResponse;
import com.shortify.urlservice.service.QrCodeService;
import com.shortify.urlservice.service.UrlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

/**
 * REST controller for URL management (CRUD operations).
 *
 * WHY TWO SEPARATE CONTROLLERS?
 * - UrlController: Handles authenticated CRUD (create, list, delete)
 * - RedirectController: Handles public redirect (no auth needed, ultra-low latency)
 *
 * Separating them allows different security rules, rate limits, and scaling.
 */
@RestController
@RequestMapping("/api/v1/urls")
@RequiredArgsConstructor
@Tag(name = "URL Management", description = "Create, read, update, delete short URLs")
@SecurityRequirement(name = "bearerAuth")
public class UrlController {

    private final UrlService urlService;
    private final QrCodeService qrCodeService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new short URL")
    public UrlResponse createUrl(
            @Valid @RequestBody CreateUrlRequest request,
            @AuthenticationPrincipal(expression = "userId") Long userId,
            @AuthenticationPrincipal(expression = "email") String userEmail
    ) {
        return urlService.createUrl(request, userId, userEmail);
    }

    @GetMapping
    @Operation(summary = "List all URLs for the authenticated user (paginated)")
    public Page<UrlResponse> listUrls(
            @AuthenticationPrincipal(expression = "userId") Long userId,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable
    ) {
        return urlService.getUserUrls(userId, pageable);
    }

    @GetMapping("/{shortCode}")
    @Operation(summary = "Get URL details by short code")
    public UrlResponse getUrl(
            @PathVariable String shortCode,
            @AuthenticationPrincipal(expression = "userId") Long userId
    ) {
        return urlService.getUrlDetails(shortCode, userId);
    }

    @DeleteMapping("/{shortCode}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Soft-delete a short URL")
    public void deleteUrl(
            @PathVariable String shortCode,
            @AuthenticationPrincipal(expression = "userId") Long userId
    ) {
        urlService.deleteUrl(shortCode, userId);
    }

    @GetMapping("/{shortCode}/qr")
    @Operation(summary = "Generate QR code for a short URL")
    public ResponseEntity<byte[]> getQrCode(
            @PathVariable String shortCode,
            @RequestParam(defaultValue = "300") int size
    ) {
        String shortUrl = urlService.getUrlDetails(shortCode, null) // public QR
                .shortUrl();

        byte[] qrBytes = qrCodeService.generateQrCode(shortUrl, size, size);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"qr-" + shortCode + ".png\"")
                .contentType(MediaType.IMAGE_PNG)
                .body(qrBytes);
    }
}
