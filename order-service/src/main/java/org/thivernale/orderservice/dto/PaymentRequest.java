package org.thivernale.orderservice.dto;

import org.thivernale.orderservice.model.PaymentMethod;

import java.math.BigDecimal;

public record PaymentRequest(
    BigDecimal amount,
    PaymentMethod paymentMethod,
    Long orderId,
    String orderReference,
    CustomerResponse customer
) {
}
