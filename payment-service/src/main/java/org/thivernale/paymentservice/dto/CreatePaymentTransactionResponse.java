package org.thivernale.paymentservice.dto;

import org.thivernale.paymentservice.model.PaymentTransactionStatus;

import java.time.LocalDateTime;

public record CreatePaymentTransactionResponse(
    Long paymentTransactionId,
    PaymentTransactionStatus status,
    String errorMessage,
    LocalDateTime executedAt
) {
}
