package com.queenstouch.queenstouchbackend.service;

import com.queenstouch.queenstouchbackend.config.AppProperties;
import com.queenstouch.queenstouchbackend.dto.response.ApiResponse;
import com.queenstouch.queenstouchbackend.exception.AppException;
import com.queenstouch.queenstouchbackend.model.Order;
import com.queenstouch.queenstouchbackend.model.Order.OrderItem;
import com.queenstouch.queenstouchbackend.model.enums.OrderStatus;
import com.queenstouch.queenstouchbackend.dto.request.CreateOrderRequest;
import com.queenstouch.queenstouchbackend.model.PricingConfig;
import com.queenstouch.queenstouchbackend.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserService userService;
    private final PricingService pricingService;

    public List<Map<String, Object>> getPricingCatalogue() {
        return pricingService.getAllPricing().stream()
                .map(config -> entry(config.getServiceKey(), config.getLabel(), config.getMinPrice(), config.getMaxPrice()))
                .toList();
    }

    public Order createOrder(String userEmail, CreateOrderRequest request) {
        var user = userService.findByEmail(userEmail);
        List<OrderItem> items = request.getServiceKeys().stream().map(key -> {
            PricingConfig config = pricingService.getPricing(key);
            return OrderItem.builder()
                    .serviceKey(key)
                    .label(config.getLabel())
                    .amountNgn(config.getMinPrice())
                    .build();
        }).toList();

        BigDecimal total = items.stream()
                .map(OrderItem::getAmountNgn)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = Order.builder()
                .userId(user.getId())
                .items(items)
                .totalAmountNgn(total)
                .status(OrderStatus.PAID)           // mock: instantly paid
                .paymentRef("MOCK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .relatedDocumentId(request.getRelatedDocumentId())
                .relatedDocumentType(request.getRelatedDocumentType())
                .createdAt(Instant.now())
                .paidAt(Instant.now())
                .build();

        return orderRepository.save(order);
    }

    public List<Order> listForUser(String userEmail) {
        var user = userService.findByEmail(userEmail);
        return orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
    }

    public Order getById(String id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("Order not found"));
    }

    public boolean hasValidOrderForDocument(String documentId) {
        return orderRepository.existsByRelatedDocumentIdAndStatusIn(
                documentId, List.of(OrderStatus.PAID));
    }

    // ── Helpers ──────────────────────────────────────────────────────────────



    private Map<String, Object> entry(String key, String label, BigDecimal min, BigDecimal max) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("serviceKey", key);
        m.put("label", label);
        m.put("minPrice", min);
        if (max != null) m.put("maxPrice", max);
        return m;
    }
}
