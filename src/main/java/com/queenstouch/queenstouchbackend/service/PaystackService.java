package com.queenstouch.queenstouchbackend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.queenstouch.queenstouchbackend.config.AppProperties;
import com.queenstouch.queenstouchbackend.dto.request.PaystackInitializeRequest;
import com.queenstouch.queenstouchbackend.dto.response.PaystackInitializeResponse;
import com.queenstouch.queenstouchbackend.dto.response.PaystackVerifyResponse;
import com.queenstouch.queenstouchbackend.exception.AppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaystackService {

    private final AppProperties appProperties;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private static final String PAYSTACK_INITIALIZE_URL = "https://api.paystack.co/transaction/initialize";
    private static final String PAYSTACK_VERIFY_URL = "https://api.paystack.co/transaction/verify/";

    public PaystackInitializeResponse initializePayment(PaystackInitializeRequest request) {
        HttpHeaders headers = getHeaders();
        HttpEntity<PaystackInitializeRequest> entity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<PaystackInitializeResponse> response = restTemplate.exchange(
                    PAYSTACK_INITIALIZE_URL,
                    HttpMethod.POST,
                    entity,
                    PaystackInitializeResponse.class
            );
            return response.getBody();
        } catch (Exception e) {
            log.error("Paystack initialization failed", e);
            throw new AppException("PAYMENT_GATEWAY_ERROR", HttpStatus.BAD_GATEWAY, "Payment gateway error: " + e.getMessage());
        }
    }

    public PaystackVerifyResponse verifyPayment(String reference) {
        HttpHeaders headers = getHeaders();
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<PaystackVerifyResponse> response = restTemplate.exchange(
                    PAYSTACK_VERIFY_URL + reference,
                    HttpMethod.GET,
                    entity,
                    PaystackVerifyResponse.class
            );
            return response.getBody();
        } catch (Exception e) {
            log.error("Paystack verification failed for ref: " + reference, e);
            throw new AppException("PAYMENT_GATEWAY_ERROR", HttpStatus.BAD_GATEWAY, "Payment verification error: " + e.getMessage());
        }
    }

    public boolean verifyWebhookSignature(String payload, String signature) {
        String secretKey = appProperties.getPaystack().getSecretKey();
        if (signature == null || payload == null || secretKey == null || secretKey.isBlank()) {
            log.warn("Webhook signature check skipped — missing payload, signature, or secret key");
            return false;
        }
        try {
            Mac mac = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            mac.init(secretKeySpec);
            byte[] hmacData = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hmacData) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString().equals(signature);
        } catch (Exception e) {
            log.error("Error verifying webhook signature", e);
            return false;
        }
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(appProperties.getPaystack().getSecretKey());
        return headers;
    }
}

