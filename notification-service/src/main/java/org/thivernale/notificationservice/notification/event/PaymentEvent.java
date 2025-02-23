package org.thivernale.notificationservice.notification.event;

import org.thivernale.notificationservice.notification.PaymentMethod;

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
