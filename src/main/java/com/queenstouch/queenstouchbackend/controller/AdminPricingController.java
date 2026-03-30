package com.queenstouch.queenstouchbackend.controller;

import com.queenstouch.queenstouchbackend.dto.request.UpdatePricingRequest;
import com.queenstouch.queenstouchbackend.dto.response.ApiResponse;
import com.queenstouch.queenstouchbackend.model.PricingConfig;
import com.queenstouch.queenstouchbackend.service.PricingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/pricing")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin Pricing", description = "Admin endpoints for managing dynamic platform pricing")
public class AdminPricingController {

    private final PricingService pricingService;

    @GetMapping
    @Operation(summary = "Get all pricing configurations", description = "Retrieves all service prices. Auto-populates defaults if any are missing in the DB.")
    public ResponseEntity<ApiResponse<List<PricingConfig>>> getAllPricing() {
        return ResponseEntity.ok(ApiResponse.success(
                "Pricing catalogue retrieved successfully",
                pricingService.getAllPricing()
        ));
    }

    @PutMapping("/{serviceKey}")
    @Operation(summary = "Update a pricing configuration", description = "Updates the min and/or max price for a specific service ID (e.g., STANDARD_CV).")
    public ResponseEntity<ApiResponse<PricingConfig>> updatePricing(
            @PathVariable String serviceKey,
            @Valid @RequestBody UpdatePricingRequest request) {
        
        PricingConfig updated = pricingService.updatePricing(
                serviceKey, 
                request.getMinPrice(), 
                request.getMaxPrice()
        );
        
        return ResponseEntity.ok(ApiResponse.success(
                "Pricing updated successfully",
                updated
        ));
    }
}
