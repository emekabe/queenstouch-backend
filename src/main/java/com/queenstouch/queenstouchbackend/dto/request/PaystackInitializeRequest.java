package com.queenstouch.queenstouchbackend.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaystackInitializeRequest {
    private String email;
    private Long amount; // in kobo
    private String reference;

    @JsonProperty("callback_url")
    private String callbackUrl;
}
