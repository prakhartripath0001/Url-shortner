package com.shortify.auth_service.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shortify.auth_service.auth.dto.*;
import com.shortify.auth_service.auth.service.AuthService;
import com.shortify.auth_service.auth.service.EmailVerificationService;
import com.shortify.auth_service.auth.service.PasswordResetService;
import com.shortify.auth_service.common.exception.AuthException;
import com.shortify.auth_service.enums.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.flyway.enabled=false",
        "jwt.secret=test-secret-key-that-is-at-least-32-chars-long!",
        "jwt.access-token-expiration=900000",
        "jwt.refresh-token-expiration=604800000",
        "spring.mail.host=localhost",
        "spring.mail.port=3025"
})
@DisplayName("AuthController Integration Tests")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean private AuthService authService;
    @MockBean private EmailVerificationService emailVerificationService;
    @MockBean private PasswordResetService passwordResetService;

    @Test
    @DisplayName("POST /api/auth/register - should return 201 Created on success")
    void shouldRegisterSuccessfully() throws Exception {
        RegisterRequest request = new RegisterRequest("newuser", "new@example.com", "password123");
        UserResponse userResponse = new UserResponse(1L, "newuser", "new@example.com", Role.ROLE_USER, false);

        when(authService.register(any())).thenReturn(userResponse);

        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.username").value("newuser"));
    }

    @Test
    @DisplayName("POST /api/auth/register - should return 400 on validation failure")
    void shouldReturnBadRequestForInvalidRegister() throws Exception {
        RegisterRequest invalid = new RegisterRequest("", "not-an-email", "123");

        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/auth/register - should return 409 when username taken")
    void shouldReturn409WhenUsernameTaken() throws Exception {
        RegisterRequest request = new RegisterRequest("takenuser", "new@example.com", "password123");
        when(authService.register(any())).thenThrow(new AuthException("Username is already taken", HttpStatus.CONFLICT));

        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Username is already taken"));
    }

    @Test
    @DisplayName("GET /api/auth/verify-email - should return 200 with valid token")
    void shouldVerifyEmailSuccessfully() throws Exception {
        mockMvc.perform(get("/api/auth/verify-email")
                        .param("token", "valid-token-value"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("POST /api/auth/forgot-password - should always return 200")
    void shouldReturn200ForForgotPassword() throws Exception {
        mockMvc.perform(post("/api/auth/forgot-password")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"unknown@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
