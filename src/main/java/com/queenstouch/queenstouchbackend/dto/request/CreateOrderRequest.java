package com.queenstouch.queenstouchbackend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class CreateOrderRequest {

    @NotEmpty(message = "At least one order item is required")
    private List<String> serviceKeys;  // e.g. ["STANDARD_CV"], ["BUNDLE_JOB_SEEKER"]

    /** Optional: document this order unlocks downloads for */
    private String relatedDocumentId;
    private String relatedDocumentType;
}
