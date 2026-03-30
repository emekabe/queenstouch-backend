package com.queenstouch.queenstouchbackend.dto.request;

import com.queenstouch.queenstouchbackend.model.enums.PremiumServiceType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreatePremiumRequestDto {

    @NotNull(message = "Service type is required")
    private PremiumServiceType serviceType;

    private String notes;
}
