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
import java.util.ArrayList;
import java.util.Comparator;
import java.time.Instant;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;


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

    @GetMapping("/recent-activity")
    @Operation(summary = "Get recent activity list")
    public ResponseEntity<ApiResponse<List<Map<String, String>>>> getRecentActivity() {
        List<Map<String, String>> activities = new ArrayList<>();
        
        orderRepository.findAll().stream()
            .sorted(Comparator.comparing(Order::getCreatedAt).reversed())
            .limit(5)
            .forEach(o -> activities.add(Map.of(
                "title", "New Order",
                "desc", "User " + o.getUserId() + " placed an order" + (o.getItems() != null && !o.getItems().isEmpty() ? " for " + o.getItems().getFirst().getLabel() : ""),
                "time", o.getCreatedAt().toString()
            )));
            
        userRepository.findAll().stream()
            .sorted(Comparator.comparing(User::getCreatedAt).reversed())
            .limit(5)
            .forEach(u -> activities.add(Map.of(
                "title", "New User Registration",
                "desc", u.getFirstName() + " created an account",
                "time", u.getCreatedAt().toString()
            )));
            
        activities.sort((a, b) -> Instant.parse(b.get("time")).compareTo(Instant.parse(a.get("time"))));
        
        return ResponseEntity.ok(ApiResponse.success(activities.stream().limit(5).toList()));
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

    @PostMapping(value = "/premium-requests/{id}/deliver", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload final CV file and mark request as COMPLETED")
    public ResponseEntity<ApiResponse<PremiumServiceRequest>> deliverPremiumRequest(
            @PathVariable String id,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "notes", required = false) String notes) {
        PremiumServiceRequest updated = premiumRequestService.adminDeliverFile(id, file, notes);
        return ResponseEntity.ok(ApiResponse.success("File delivered and status updated to COMPLETED", updated));
    }
}
