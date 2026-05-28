package com.queenstouch.queenstouchbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class WebConfig {

    /**
     * Exposes a shared RestTemplate bean so it can be injected and mocked in tests.
     * Used by PaystackService for outbound API calls.
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
