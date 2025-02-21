package org.thivernale.orderservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.thivernale.orderservice.dto.PaymentRequest;

@FeignClient(name = "PAYMENT-SERVICE", path = "/api/payment")
public interface PaymentClient {
    @PostMapping
    Long createPayment(@RequestBody PaymentRequest paymentRequest);
}
