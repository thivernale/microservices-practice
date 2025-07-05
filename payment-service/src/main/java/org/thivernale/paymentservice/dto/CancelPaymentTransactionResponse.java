package org.thivernale.paymentservice.dto;

import org.thivernale.paymentservice.model.PaymentTransactionStatus;

import java.time.LocalDateTime;

public record CancelPaymentTransactionResponse(
    Long refundId,
    PaymentTransactionStatus status,
    String errorMessage,
    LocalDateTime executedAt
) {
}
