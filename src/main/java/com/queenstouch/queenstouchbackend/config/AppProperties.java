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
    private GoogleCloudStorage googleCloudStorage = new GoogleCloudStorage();
    private String adminSecret;
    private String frontendUrl;
    private String mailFromAddress;
    private String mailPersonal;
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
    public static class GoogleCloudStorage {
        private String projectId;
        private String bucket;
        private String credentialsBase64;
    }
}
