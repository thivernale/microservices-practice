package org.thivernale.notificationservice.email;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EmailTemplate {
    PAYMENT_CONFIRMATION("payment-confirmation", "Payment successfully processed"),
    ORDER_CONFIRMATION("order-confirmation", "Order confirmation");

    private final String template;
    private final String subject;
}
