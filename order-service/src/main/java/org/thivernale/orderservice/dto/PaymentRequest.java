package org.thivernale.orderservice.dto;

import org.thivernale.orderservice.model.PaymentMethod;

import java.math.BigDecimal;

public record PaymentRequest(
    Long orderId,
    String orderReference,
    BigDecimal amount,
    PaymentMethod paymentMethod,
    CustomerResponse customer
) {
}
