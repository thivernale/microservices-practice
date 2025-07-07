package org.thivernale.paymentservice.payment.dto;

import org.thivernale.paymentservice.payment.PaymentMethod;

import java.math.BigDecimal;

public record PaymentResponse(
    Long id,
    BigDecimal amount,
    PaymentMethod paymentMethod,
    Long orderId
) {
}
