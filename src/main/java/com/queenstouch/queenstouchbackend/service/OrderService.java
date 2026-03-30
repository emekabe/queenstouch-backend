package com.queenstouch.queenstouchbackend.service;

import com.queenstouch.queenstouchbackend.config.AppProperties;
import com.queenstouch.queenstouchbackend.dto.response.ApiResponse;
import com.queenstouch.queenstouchbackend.exception.AppException;
import com.queenstouch.queenstouchbackend.model.Order;
import com.queenstouch.queenstouchbackend.model.Order.OrderItem;
import com.queenstouch.queenstouchbackend.model.enums.OrderStatus;
import com.queenstouch.queenstouchbackend.dto.request.CreateOrderRequest;
import com.queenstouch.queenstouchbackend.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserService userService;
    private final AppProperties appProperties;

    public List<Map<String, Object>> getPricingCatalogue() {
        var p = appProperties.getPricing();
        return List.of(
            entry("STANDARD_CV",                "Standard CV Builder",                 p.getStandardCv(), null),
            entry("ACADEMIC_CV",                "Academic CV Builder",                 p.getAcademicCv(), null),
            entry("COVER_LETTER",               "Cover Letter Generator",              p.getCoverLetter(), null),
            entry("LINKEDIN_GENERATOR",         "LinkedIn Profile Generator",          p.getLinkedinGenerator(), null),
            entry("JOB_MATCH_OPTIMIZE",         "CV Job Description Optimization",     p.getJobMatchOptimize(), null),
            entry("EXPERT_REVIEW",              "Expert CV Review",                    p.getExpertReviewMin(), p.getExpertReviewMax()),
            entry("PRO_CV_WRITING",             "Professional CV Writing (Expert)",    p.getProCvWritingMin(), p.getProCvWritingMax()),
            entry("PERSONAL_STATEMENT",         "Personal Statement Writing",          p.getPersonalStatementMin(), p.getPersonalStatementMax()),
            entry("MOTIVATION_LETTER",          "Motivation Letter Writing",           p.getMotivationLetterMin(), p.getMotivationLetterMax()),
            entry("LINKEDIN_OPTIMIZATION",      "LinkedIn Profile Optimization (Expert)", p.getLinkedinOptimizationMin(), p.getLinkedinOptimizationMax()),
            entry("BUNDLE_JOB_SEEKER",          "Job Seeker Bundle (CV + Cover Letter)", p.getBundleJobSeeker(), null),
            entry("BUNDLE_INTERNATIONAL_STUDY", "International Study Bundle (Academic CV + Cover Letter)", p.getBundleInternationalStudy(), null),
            entry("BUNDLE_CAREER_UPGRADE",      "Career Upgrade Bundle (CV + Cover Letter + LinkedIn)", p.getBundleCareerUpgrade(), null)
        );
    }

    public Order createOrder(String userEmail, CreateOrderRequest request) {
        var user = userService.findByEmail(userEmail);
        var catalogue = buildCatalogueMap();

        List<OrderItem> items = request.getServiceKeys().stream().map(key -> {
            var entry = catalogue.get(key);
            if (entry == null) throw AppException.badRequest("Unknown service key: " + key);
            return OrderItem.builder()
                    .serviceKey(key)
                    .label((String) entry.get("label"))
                    .amountNgn((BigDecimal) entry.get("minPrice"))
                    .build();
        }).collect(Collectors.toList());

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

    private Map<String, Map<String, Object>> buildCatalogueMap() {
        Map<String, Map<String, Object>> map = new LinkedHashMap<>();
        getPricingCatalogue().forEach(e -> {
            Map<String, Object> item = new HashMap<>();
            item.put("label", e.get("label"));
            item.put("minPrice", e.get("minPrice"));
            map.put((String) e.get("serviceKey"), item);
        });
        return map;
    }

    private Map<String, Object> entry(String key, String label, BigDecimal min, BigDecimal max) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("serviceKey", key);
        m.put("label", label);
        m.put("minPrice", min);
        if (max != null) m.put("maxPrice", max);
        return m;
    }
}
