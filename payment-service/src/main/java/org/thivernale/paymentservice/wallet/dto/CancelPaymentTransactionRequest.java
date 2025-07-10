package org.thivernale.paymentservice.wallet.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CancelPaymentTransactionRequest(
    @NotNull(message = "Payment transaction cannot be empty")
    Long paymentTransactionId,
    @Positive(message = "Refund amount should be positive")
    @NotNull(message = "Refund amount should be positive")
    BigDecimal amount,
    String reason
) {
}
