package com.queenstouch.queenstouchbackend.service;

import com.queenstouch.queenstouchbackend.exception.AppException;
import com.queenstouch.queenstouchbackend.model.PricingConfig;
import com.queenstouch.queenstouchbackend.repository.PricingRepository;
import com.queenstouch.queenstouchbackend.util.PricingDefaults;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PricingService {

    private final PricingRepository pricingRepository;

    /**
     * Gets a pricing config from DB. If not found, populates from defaults and saves.
     */
    public PricingConfig getPricing(String serviceKey) {
        return pricingRepository.findById(serviceKey).orElseGet(() -> {
            PricingConfig defaultConfig = PricingDefaults.DEFAULTS.get(serviceKey);
            if (defaultConfig == null) {
                throw AppException.badRequest("Unknown service key: " + serviceKey);
            }
            log.info("Pricing not found in DB for [{}]. Auto-populating default.", serviceKey);
            PricingConfig newConfig = defaultConfig.toBuilder()
                    .updatedAt(Instant.now())
                    .build();
            return pricingRepository.save(newConfig);
        });
    }

    /**
     * Iterates all requested default keys, ensuring they are populated in DB, returning the full list.
     */
    public List<PricingConfig> getAllPricing() {
        List<PricingConfig> results = new ArrayList<>();
        // Iterate over DEFAULTS map keys to guarantee ordering and completeness
        for (String key : PricingDefaults.DEFAULTS.keySet()) {
            results.add(getPricing(key));
        }
        return results;
    }

    /**
     * Admin action to update the price of a specific service.
     */
    public PricingConfig updatePricing(String serviceKey, BigDecimal minPrice, BigDecimal maxPrice) {
        PricingConfig current = getPricing(serviceKey);
        
        if (minPrice == null || minPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw AppException.badRequest("minPrice must be greater than or equal to 0");
        }
        
        if (maxPrice != null && maxPrice.compareTo(minPrice) < 0) {
            throw AppException.badRequest("maxPrice cannot be less than minPrice");
        }

        PricingConfig updated = current.toBuilder()
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .build();
        
        log.info("Admin updated pricing for [{}]: min={}, max={}", serviceKey, minPrice, maxPrice);
        return pricingRepository.save(updated);
    }
}
