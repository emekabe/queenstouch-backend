package com.queenstouch.queenstouchbackend.dto.request;

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
    private String callback_url;
}
