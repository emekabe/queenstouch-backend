package com.queenstouch.queenstouchbackend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Data
@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private Jwt jwt = new Jwt();
    private Storage storage = new Storage();
    private Pricing pricing = new Pricing();

    @Data
    public static class Jwt {
        private String secret;
        private long accessTokenExpiryMs;
        private long refreshTokenExpiryMs;
    }

    @Data
    public static class Storage {
        private String uploadDir;
    }

    @Data
    public static class Pricing {
        private BigDecimal standardCv;
        private BigDecimal academicCv;
        private BigDecimal coverLetter;
        private BigDecimal linkedinGenerator;
        private BigDecimal jobMatchOptimize;
        private BigDecimal expertReviewMin;
        private BigDecimal expertReviewMax;
        private BigDecimal proCvWritingMin;
        private BigDecimal proCvWritingMax;
        private BigDecimal personalStatementMin;
        private BigDecimal personalStatementMax;
        private BigDecimal motivationLetterMin;
        private BigDecimal motivationLetterMax;
        private BigDecimal linkedinOptimizationMin;
        private BigDecimal linkedinOptimizationMax;
        private BigDecimal bundleJobSeeker;
        private BigDecimal bundleInternationalStudy;
        private BigDecimal bundleCareerUpgrade;
    }
}
