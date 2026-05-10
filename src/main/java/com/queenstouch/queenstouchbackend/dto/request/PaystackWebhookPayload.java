package com.queenstouch.queenstouchbackend.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaystackWebhookPayload {

    private String event;
    private Data data;

    @lombok.Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Data {
        private String id;
        private String domain;
        private String status;
        private String reference;
        private Long amount;
        private String gateway_response;
        private String paid_at;
        private String channel;
        private String currency;
        private String ip_address;
    }
}
