package com.queenstouch.queenstouchbackend.util;

import com.queenstouch.queenstouchbackend.model.PricingConfig;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Hardcoded pricing defaults. If a price is requested but doesn't exist in MongoDB,
 * it will be retrieved from here and populated automatically.
 */
public final class PricingDefaults {

    private PricingDefaults() {}

    public static final Map<String, PricingConfig> DEFAULTS = new LinkedHashMap<>();

    static {
        addConfig("STANDARD_CV", "Standard CV Builder", "5000", null);
        addConfig("ACADEMIC_CV", "Academic CV Builder", "6000", null);
        addConfig("COVER_LETTER", "Cover Letter Generator", "2000", null);
        addConfig("LINKEDIN_GENERATOR", "LinkedIn Profile Generator", "2500", null);
        addConfig("JOB_MATCH_OPTIMIZE", "CV Job Description Optimization", "3000", null);
        
        addConfig("EXPERT_REVIEW", "Expert CV Review", "5000", "10000");
        addConfig("PRO_CV_WRITING", "Professional CV Writing (Expert)", "20000", "40000");
        addConfig("PERSONAL_STATEMENT", "Personal Statement Writing", "30000", "50000");
        addConfig("MOTIVATION_LETTER", "Motivation Letter Writing", "20000", "40000");
        addConfig("LINKEDIN_OPTIMIZATION", "LinkedIn Profile Optimization (Expert)", "30000", "40000");
        
        addConfig("BUNDLE_JOB_SEEKER", "Job Seeker Bundle (CV + Cover Letter)", "7000", null);
        addConfig("BUNDLE_INTERNATIONAL_STUDY", "International Study Bundle (Academic CV + Cover Letter)", "10000", null);
        addConfig("BUNDLE_CAREER_UPGRADE", "Career Upgrade Bundle (CV + Cover Letter + LinkedIn)", "12000", null);
    }

    private static void addConfig(String key, String label, String min, String max) {
        DEFAULTS.put(key, PricingConfig.builder()
                .serviceKey(key)
                .label(label)
                .minPrice(new BigDecimal(min))
                .maxPrice(max != null ? new BigDecimal(max) : null)
                .build());
    }
}
