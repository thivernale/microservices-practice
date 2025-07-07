package org.thivernale.paymentservice.wallet.dto;

import org.thivernale.paymentservice.wallet.model.PaymentTransactionStatus;

import java.time.LocalDateTime;

public record CancelPaymentTransactionResponse(
    Long refundId,
    PaymentTransactionStatus status,
    String errorMessage,
    LocalDateTime executedAt
) {
}
