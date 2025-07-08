package org.thivernale.paymentservice.wallet.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreatePaymentTransactionRequest(
    @Positive(message = "Payment transaction amount should be positive")
    @NotNull(message = "Payment transaction amount should be positive")
    BigDecimal amount,
    @NotNull(message = "Currency should be specified")
    String currency,
    @NotNull(message = "Source account cannot be empty")
    Long sourceCurrencyAccountId,
    Long destCurrencyAccountId,
    String note
) {
}
