package com.queenstouch.queenstouchbackend.dto.request;

import jakarta.validation.constraints.DecimalMin;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdatePricingRequest {
    
    @DecimalMin(value = "0.0", message = "minPrice must be greater than or equal to 0")
    private BigDecimal minPrice;
    
    private BigDecimal maxPrice;
}
