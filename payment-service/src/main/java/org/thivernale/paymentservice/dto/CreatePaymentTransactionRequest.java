package org.thivernale.paymentservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.thivernale.paymentservice.model.PaymentMethod;

import java.math.BigDecimal;

public record CreatePaymentTransactionRequest(
    @Positive(message = "Payment amount should be positive")
    @NotNull(message = "Payment amount should be positive")
    BigDecimal amount,
    @NotNull(message = "Currency should be specified")
    String currency,
    @NotNull(message = "Payment method should be specified")
    PaymentMethod paymentMethod,
    @NotNull(message = "Source bank account cannot be empty")
    Long sourceBankAccountId,
    Long destBankAccountId,
    String note
) {
}
