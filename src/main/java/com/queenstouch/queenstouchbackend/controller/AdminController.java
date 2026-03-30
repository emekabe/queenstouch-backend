package com.queenstouch.queenstouchbackend.controller;

import com.queenstouch.queenstouchbackend.dto.response.ApiResponse;
import com.queenstouch.queenstouchbackend.model.Order;
import com.queenstouch.queenstouchbackend.model.PremiumServiceRequest;
import com.queenstouch.queenstouchbackend.model.User;
import com.queenstouch.queenstouchbackend.model.enums.RequestStatus;
import com.queenstouch.queenstouchbackend.repository.OrderRepository;
import com.queenstouch.queenstouchbackend.repository.UserRepository;
import com.queenstouch.queenstouchbackend.service.PremiumRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin", description = "Administrator APIs — ADMIN role required")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final PremiumRequestService premiumRequestService;

    // ── Users ─────────────────────────────────────────────────────────────────

    @GetMapping("/users")
    @Operation(summary = "List all users")
    public ResponseEntity<ApiResponse<List<User>>> listUsers() {
        return ResponseEntity.ok(ApiResponse.success(userRepository.findAll()));
    }

    @GetMapping("/stats")
    @Operation(summary = "Basic platform stats")
    public ResponseEntity<ApiResponse<Map<String, Long>>> stats() {
        return ResponseEntity.ok(ApiResponse.success(Map.<String, Long>of(
                "totalUsers", userRepository.count(),
                "totalOrders", orderRepository.count(),
                "totalPremiumRequests", (long) premiumRequestService.listAll().size()
        )));
    }

    // ── Orders ────────────────────────────────────────────────────────────────

    @GetMapping("/orders")
    @Operation(summary = "List all orders")
    public ResponseEntity<ApiResponse<List<Order>>> listOrders() {
        return ResponseEntity.ok(ApiResponse.success(orderRepository.findAll()));
    }

    // ── Premium Requests ──────────────────────────────────────────────────────

    @GetMapping("/premium-requests")
    @Operation(summary = "List all premium service requests")
    public ResponseEntity<ApiResponse<List<PremiumServiceRequest>>> listPremiumRequests() {
        return ResponseEntity.ok(ApiResponse.success(premiumRequestService.listAll()));
    }

    @PutMapping("/premium-requests/{id}/status")
    @Operation(summary = "Update the status of a premium service request")
    public ResponseEntity<ApiResponse<PremiumServiceRequest>> updatePremiumRequestStatus(
            @PathVariable String id,
            @RequestBody StatusUpdateDto dto) {
        PremiumServiceRequest updated = premiumRequestService.updateStatus(id, dto.getStatus(), dto.getAdminNotes());
        return ResponseEntity.ok(ApiResponse.success("Status updated", updated));
    }

    @Data
    public static class StatusUpdateDto {
        private RequestStatus status;
        private String adminNotes;
    }
}
