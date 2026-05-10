package com.queenstouch.queenstouchbackend.controller;

import com.queenstouch.queenstouchbackend.dto.request.CreateOrderRequest;
import com.queenstouch.queenstouchbackend.dto.request.PaystackWebhookPayload;
import com.queenstouch.queenstouchbackend.dto.response.ApiResponse;
import com.queenstouch.queenstouchbackend.dto.response.CreateOrderResponse;
import com.queenstouch.queenstouchbackend.model.Order;
import com.queenstouch.queenstouchbackend.service.OrderService;
import com.queenstouch.queenstouchbackend.service.PaystackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Orders & Pricing", description = "Pricing catalogue and order management")
@Slf4j
public class OrderController {

    private final OrderService orderService;
    private final PaystackService paystackService;

    @GetMapping("/pricing")
    @Operation(summary = "Get the full pricing catalogue (public)")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getPricing() {
        return ResponseEntity.ok(ApiResponse.success(orderService.getPricingCatalogue()));
    }

    @PostMapping("/webhook/payment")
    @Operation(summary = "Paystack payment webhook")
    public ResponseEntity<ApiResponse<Void>> paymentWebhook(
            @RequestHeader("x-paystack-signature") String signature,
            @RequestBody String rawPayload) {
        
        boolean isValid = paystackService.verifyWebhookSignature(rawPayload, signature);
        if (!isValid) {
            log.warn("Invalid Paystack webhook signature");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            PaystackWebhookPayload payload = mapper.readValue(rawPayload, PaystackWebhookPayload.class);
            
            if ("charge.success".equals(payload.getEvent()) && payload.getData() != null) {
                orderService.processSuccessfulPayment(payload.getData().getReference());
            }
        } catch (Exception e) {
            log.error("Failed to process webhook payload", e);
        }

        return ResponseEntity.ok(ApiResponse.success("Webhook received", null));
    }

    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Create an order and initialize payment")
    public ResponseEntity<ApiResponse<CreateOrderResponse>> create(
            @AuthenticationPrincipal String email,
            @Valid @RequestBody CreateOrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Order created", orderService.createOrder(email, request)));
    }

    @GetMapping("/verify-payment")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Explicitly verify payment status from Paystack (frontend callback)")
    public ResponseEntity<ApiResponse<Void>> verifyPayment(@RequestParam String reference) {
        var verifyRes = paystackService.verifyPayment(reference);
        if (verifyRes.isStatus() && verifyRes.getData() != null && "success".equalsIgnoreCase(verifyRes.getData().getStatus())) {
            orderService.processSuccessfulPayment(reference);
            return ResponseEntity.ok(ApiResponse.success("Payment verified successfully", null));
        }
        return ResponseEntity.badRequest().body(ApiResponse.error(String.valueOf(HttpStatus.BAD_REQUEST.value()), "Payment not successful"));
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
