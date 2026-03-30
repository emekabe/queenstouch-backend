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
    private String adminSecret;
    private String frontendUrl;
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
}
