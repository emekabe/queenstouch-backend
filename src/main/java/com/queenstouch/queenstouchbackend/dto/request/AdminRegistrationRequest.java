package com.queenstouch.queenstouchbackend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AdminRegistrationRequest extends RegisterRequest {

    @NotBlank(message = "Admin secret is required for admin registration")
    private String adminSecret;
}
