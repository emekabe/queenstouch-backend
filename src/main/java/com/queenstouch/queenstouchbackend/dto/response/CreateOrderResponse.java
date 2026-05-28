package com.queenstouch.queenstouchbackend.dto.response;

import com.queenstouch.queenstouchbackend.model.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderResponse {
    private Order order;
    private String paymentUrl;
    private String accessCode;
}
