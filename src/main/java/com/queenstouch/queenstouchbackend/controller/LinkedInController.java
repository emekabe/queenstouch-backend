package com.queenstouch.queenstouchbackend.controller;

import com.queenstouch.queenstouchbackend.dto.request.GenerateLinkedInRequest;
import com.queenstouch.queenstouchbackend.dto.response.ApiResponse;
import com.queenstouch.queenstouchbackend.model.LinkedInProfile;
import com.queenstouch.queenstouchbackend.service.LinkedInProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/linkedin")
@RequiredArgsConstructor
@Tag(name = "LinkedIn Profile Generator", description = "AI-powered LinkedIn profile generation")
@SecurityRequirement(name = "bearerAuth")
public class LinkedInController {

    private final LinkedInProfileService linkedInProfileService;

    @PostMapping("/generate")
    @Operation(summary = "Generate a LinkedIn headline, summary, and skills from career input")
    public ResponseEntity<ApiResponse<LinkedInProfile>> generate(
            @AuthenticationPrincipal String email,
            @Valid @RequestBody GenerateLinkedInRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("LinkedIn profile generated", linkedInProfileService.generate(email, request)));
    }

    @GetMapping
    @Operation(summary = "List all LinkedIn profiles for the current user")
    public ResponseEntity<ApiResponse<List<LinkedInProfile>>> list(@AuthenticationPrincipal String email) {
        return ResponseEntity.ok(ApiResponse.success(linkedInProfileService.listForUser(email)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a specific LinkedIn profile")
    public ResponseEntity<ApiResponse<LinkedInProfile>> get(
            @AuthenticationPrincipal String email,
            @PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(linkedInProfileService.getForUser(email, id)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a LinkedIn profile")
    public ResponseEntity<ApiResponse<Void>> delete(
            @AuthenticationPrincipal String email,
            @PathVariable String id) {
        linkedInProfileService.delete(email, id);
        return ResponseEntity.ok(ApiResponse.success("LinkedIn profile deleted", null));
    }
}
