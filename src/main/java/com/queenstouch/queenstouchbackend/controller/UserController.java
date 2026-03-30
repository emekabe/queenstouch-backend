package com.queenstouch.queenstouchbackend.controller;

import com.queenstouch.queenstouchbackend.dto.request.ChangePasswordRequest;
import com.queenstouch.queenstouchbackend.dto.request.UpdateProfileRequest;
import com.queenstouch.queenstouchbackend.dto.response.ApiResponse;
import com.queenstouch.queenstouchbackend.dto.response.UserResponse;
import com.queenstouch.queenstouchbackend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Profile", description = "Manage logged-in user profile")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "Get current user's profile")
    public ResponseEntity<ApiResponse<UserResponse>> getProfile(@AuthenticationPrincipal String email) {
        return ResponseEntity.ok(ApiResponse.success(userService.getProfile(email)));
    }

    @PutMapping("/me")
    @Operation(summary = "Update name and/or phone number")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            @AuthenticationPrincipal String email,
            @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Profile updated", userService.updateProfile(email, request)));
    }

    @PutMapping("/me/password")
    @Operation(summary = "Change password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @AuthenticationPrincipal String email,
            @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(email, request);
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully", null));
    }
}
