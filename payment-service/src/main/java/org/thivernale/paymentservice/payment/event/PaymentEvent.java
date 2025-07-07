package org.thivernale.paymentservice.payment.event;

import org.thivernale.paymentservice.payment.PaymentMethod;

import java.math.BigDecimal;

public record PaymentEvent(
    String orderReference,
    BigDecimal amount,
    PaymentMethod paymentMethod,
    String customerFirstName,
    String customerLastName,
    String customerEmail
) {
}
