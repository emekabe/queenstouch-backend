package com.queenstouch.queenstouchbackend.controller;

import com.queenstouch.queenstouchbackend.dto.request.CreatePremiumRequestDto;
import com.queenstouch.queenstouchbackend.dto.response.ApiResponse;
import com.queenstouch.queenstouchbackend.model.PremiumServiceRequest;
import com.queenstouch.queenstouchbackend.service.PremiumRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/premium-requests")
@RequiredArgsConstructor
@Tag(name = "Premium Service Requests", description = "Submit and manage human-written document requests")
@SecurityRequirement(name = "bearerAuth")
public class PremiumRequestController {

    private final PremiumRequestService premiumRequestService;

    @PostMapping
    @Operation(summary = "Submit a premium service request")
    public ResponseEntity<ApiResponse<PremiumServiceRequest>> create(
            @AuthenticationPrincipal String email,
            @Valid @RequestBody CreatePremiumRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Request submitted successfully", premiumRequestService.create(email, request)));
    }

    @GetMapping
    @Operation(summary = "List all your premium service requests")
    public ResponseEntity<ApiResponse<List<PremiumServiceRequest>>> list(@AuthenticationPrincipal String email) {
        return ResponseEntity.ok(ApiResponse.success(premiumRequestService.listForUser(email)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a specific premium service request")
    public ResponseEntity<ApiResponse<PremiumServiceRequest>> get(
            @AuthenticationPrincipal String email,
            @PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(premiumRequestService.getForUser(email, id)));
    }

    @PostMapping(value = "/{id}/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload a supporting file to a premium request")
    public ResponseEntity<ApiResponse<PremiumServiceRequest>> upload(
            @AuthenticationPrincipal String email,
            @PathVariable String id,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(ApiResponse.success("File uploaded", premiumRequestService.addUploadedFile(email, id, file)));
    }
}
