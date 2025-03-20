package org.thivernale.orderservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.thivernale.orderservice.dto.PaymentRequest;

@FeignClient(
    name = "payment-service",
    path = "/api/payment",
    url = "${app.urls.payment-service:http://payment-service}"
)
public interface PaymentClient {
    @PostMapping
    Long createPayment(@RequestBody PaymentRequest paymentRequest);
}
