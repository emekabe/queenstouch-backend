package com.queenstouch.queenstouchbackend.service;

import com.queenstouch.queenstouchbackend.config.AppProperties;
import com.queenstouch.queenstouchbackend.dto.response.CreateOrderResponse;
import com.queenstouch.queenstouchbackend.dto.request.PaystackInitializeRequest;
import com.queenstouch.queenstouchbackend.dto.response.PaystackInitializeResponse;
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
    private final PaystackService paystackService;
    private final AppProperties appProperties;
    private final EmailService emailService;

    public List<Map<String, Object>> getPricingCatalogue() {
        return pricingService.getAllPricing().stream()
                .map(config -> entry(config.getServiceKey(), config.getLabel(), config.getMinPrice(), config.getMaxPrice()))
                .toList();
    }

    public CreateOrderResponse createOrder(String userEmail, CreateOrderRequest request) {
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

        String reference = "QT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Order order = Order.builder()
                .userId(user.getId())
                .items(items)
                .totalAmountNgn(total)
                .status(OrderStatus.PENDING)
                .paymentRef(reference)
                .relatedDocumentId(request.getRelatedDocumentId())
                .relatedDocumentType(request.getRelatedDocumentType())
                .createdAt(Instant.now())
                .build();

        order = orderRepository.save(order);

        // Initialize Paystack Payment
        String callbackUrl = appProperties.getFrontendUrl() + "/orders/callback";
        long amountInKobo = total.multiply(BigDecimal.valueOf(100)).longValue();
        
        PaystackInitializeRequest paystackReq = PaystackInitializeRequest.builder()
                .email(user.getEmail())
                .amount(amountInKobo)
                .reference(reference)
                .callbackUrl(callbackUrl)
                .build();

        PaystackInitializeResponse paystackRes = paystackService.initializePayment(paystackReq);

        return CreateOrderResponse.builder()
                .order(order)
                .paymentUrl(paystackRes.getData().getAuthorization_url())
                .accessCode(paystackRes.getData().getAccess_code())
                .build();
    }

    public void processSuccessfulPayment(String reference) {
        Order order = orderRepository.findByPaymentRef(reference)
                .orElse(null);
        if (order != null && order.getStatus() == OrderStatus.PENDING) {
            order.setStatus(OrderStatus.PAID);
            order.setPaidAt(Instant.now());
            orderRepository.save(order);
            
            // Send Email Receipt
            var user = userService.findById(order.getUserId());
            String documentType = "CV".equalsIgnoreCase(order.getRelatedDocumentType()) ? "Resume / CV" : "Cover Letter";
            emailService.sendPaymentReceiptEmail(user.getEmail(), user.getFirstName(), documentType, order.getTotalAmountNgn().toString(), order.getPaymentRef());
        }
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
