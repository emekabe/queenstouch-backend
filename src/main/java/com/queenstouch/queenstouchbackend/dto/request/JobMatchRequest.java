package com.queenstouch.queenstouchbackend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class JobMatchRequest {

    @NotBlank(message = "Job description is required")
    private String jobDescription;
}
