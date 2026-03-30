package com.queenstouch.queenstouchbackend.controller;

import com.queenstouch.queenstouchbackend.dto.request.*;
import com.queenstouch.queenstouchbackend.dto.response.ApiResponse;
import com.queenstouch.queenstouchbackend.dto.response.AuthResponse;
import com.queenstouch.queenstouchbackend.dto.response.UserResponse;
import com.queenstouch.queenstouchbackend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Register, login, OTP verification, password reset")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user account")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody RegisterRequest request) {
        UserResponse user = authService.register(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Registration successful. Check your email for the verification OTP.", user));
    }

    @PostMapping("/verify-email")
    @Operation(summary = "Verify email using the OTP sent during registration")
    public ResponseEntity<ApiResponse<Void>> verifyEmail(@Valid @RequestBody VerifyEmailRequest request) {
        authService.verifyEmail(request);
        return ResponseEntity.ok(ApiResponse.success("Email verified successfully. You can now log in.", null));
    }

    @PostMapping("/login")
    @Operation(summary = "Login and receive access + refresh tokens")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse auth = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", auth));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Exchange a refresh token for a new access token")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse auth = authService.refresh(request);
        return ResponseEntity.ok(ApiResponse.success("Token refreshed", auth));
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Request a password reset OTP")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ResponseEntity.ok(ApiResponse.success("Password reset OTP sent to your email.", null));
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset password using OTP")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(ApiResponse.success("Password reset successfully. Please log in.", null));
    }
}
