package com.queenstouch.queenstouchbackend.controller;

import com.queenstouch.queenstouchbackend.dto.request.CreateOrderRequest;
import com.queenstouch.queenstouchbackend.dto.response.ApiResponse;
import com.queenstouch.queenstouchbackend.model.Order;
import com.queenstouch.queenstouchbackend.service.OrderService;
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
import java.util.Map;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Orders & Pricing", description = "Pricing catalogue and order management (mocked)")
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/pricing")
    @Operation(summary = "Get the full pricing catalogue (public)")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getPricing() {
        return ResponseEntity.ok(ApiResponse.success(orderService.getPricingCatalogue()));
    }

    @PostMapping("/webhook/payment")
    @Operation(summary = "Mock payment webhook stub (no-op)")
    public ResponseEntity<ApiResponse<Void>> paymentWebhook(@RequestBody(required = false) Object body) {
        // No-op placeholder for future payment gateway webhook
        return ResponseEntity.ok(ApiResponse.success("Webhook received", null));
    }

    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Create an order (mock: auto-confirmed as PAID)")
    public ResponseEntity<ApiResponse<Order>> create(
            @AuthenticationPrincipal String email,
            @Valid @RequestBody CreateOrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Order created and marked as paid", orderService.createOrder(email, request)));
    }

    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "List all orders for the current user")
    public ResponseEntity<ApiResponse<List<Order>>> list(@AuthenticationPrincipal String email) {
        return ResponseEntity.ok(ApiResponse.success(orderService.listForUser(email)));
    }

    @GetMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get a specific order")
    public ResponseEntity<ApiResponse<Order>> get(
            @AuthenticationPrincipal String email,
            @PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(orderService.getById(id)));
    }
}
