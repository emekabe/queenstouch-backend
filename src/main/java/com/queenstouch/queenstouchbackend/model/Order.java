package com.queenstouch.queenstouchbackend.model;

import com.queenstouch.queenstouchbackend.model.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "orders")
public class Order {

    @Id
    private String id;

    @Indexed
    private String userId;

    private List<OrderItem> items;

    private BigDecimal totalAmountNgn;

    @Builder.Default
    private OrderStatus status = OrderStatus.PAID;  // mock: always paid immediately

    private String paymentRef;

    /** Optional link to the document this order unlocks */
    private String relatedDocumentId;
    private String relatedDocumentType;   // e.g. "CV", "COVER_LETTER", "LINKEDIN"

    @Builder.Default
    @CreatedDate
    private Instant createdAt = Instant.now();

    private Instant paidAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItem {
        private String serviceKey;
        private String label;
        private BigDecimal amountNgn;
    }
}
