package org.thivernale.paymentservice.event;

import org.thivernale.paymentservice.model.PaymentMethod;

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
