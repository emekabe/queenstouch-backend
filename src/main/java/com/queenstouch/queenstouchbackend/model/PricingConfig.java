package com.queenstouch.queenstouchbackend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "pricing_configs")
public class PricingConfig {

    @Id
    private String serviceKey;

    private String label;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;

    @Builder.Default
    @LastModifiedDate
    private Instant updatedAt = Instant.now();
}
