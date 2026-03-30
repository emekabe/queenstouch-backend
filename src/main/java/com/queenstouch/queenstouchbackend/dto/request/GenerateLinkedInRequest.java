package com.queenstouch.queenstouchbackend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GenerateLinkedInRequest {

    @NotBlank(message = "Career summary input is required")
    private String careerSummaryInput;
}
