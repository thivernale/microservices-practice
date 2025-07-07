package org.thivernale.paymentservice.payment.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.thivernale.paymentservice.payment.PaymentMethod;

import java.math.BigDecimal;

public record PaymentRequest(
    Long id,
    @Positive(message = "Order amount should be positive")
    @NotNull(message = "Order amount should be positive")
    BigDecimal amount,
    @NotNull(message = "Payment method should be specified")
    PaymentMethod paymentMethod,
    Long orderId,
    String orderReference,
    Customer customer
) {
}
