package org.thivernale.paymentservice.dto;

import org.thivernale.paymentservice.model.PaymentMethod;

import java.math.BigDecimal;

public record PaymentResponse(
    Long id,
    BigDecimal amount,
    PaymentMethod paymentMethod,
    Long orderId
) {
}
