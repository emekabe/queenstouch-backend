package com.queenstouch.queenstouchbackend.controller;

import com.queenstouch.queenstouchbackend.dto.request.CreateCoverLetterRequest;
import com.queenstouch.queenstouchbackend.dto.response.ApiResponse;
import com.queenstouch.queenstouchbackend.model.CoverLetter;
import com.queenstouch.queenstouchbackend.service.CoverLetterService;
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
@RequestMapping("/api/v1/cover-letters")
@RequiredArgsConstructor
@Tag(name = "Cover Letter", description = "AI-powered cover letter generation")
@SecurityRequirement(name = "bearerAuth")
public class CoverLetterController {

    private final CoverLetterService coverLetterService;

    @PostMapping
    @Operation(summary = "Generate a cover letter using AI")
    public ResponseEntity<ApiResponse<CoverLetter>> create(
            @AuthenticationPrincipal String email,
            @Valid @RequestBody CreateCoverLetterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Cover letter generated", coverLetterService.create(email, request)));
    }

    @GetMapping
    @Operation(summary = "List all cover letters for the current user")
    public ResponseEntity<ApiResponse<List<CoverLetter>>> list(@AuthenticationPrincipal String email) {
        return ResponseEntity.ok(ApiResponse.success(coverLetterService.listForUser(email)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a specific cover letter")
    public ResponseEntity<ApiResponse<CoverLetter>> get(
            @AuthenticationPrincipal String email,
            @PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(coverLetterService.getForUser(email, id)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a cover letter")
    public ResponseEntity<ApiResponse<Void>> delete(
            @AuthenticationPrincipal String email,
            @PathVariable String id) {
        coverLetterService.delete(email, id);
        return ResponseEntity.ok(ApiResponse.success("Cover letter deleted", null));
    }
}
