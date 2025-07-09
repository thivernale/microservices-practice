package org.thivernale.paymentservice.wallet.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
public class InsufficientOutstandingAmountException extends RuntimeException {
    private final String message;
    private final Long paymentTransactionId;
    private final BigDecimal outstandingAmount;
    private final BigDecimal amount;

    public InsufficientOutstandingAmountException(Long paymentTransactionId, BigDecimal outstandingAmount, BigDecimal amount) {
        super();
        this.paymentTransactionId = paymentTransactionId;
        this.outstandingAmount = outstandingAmount;
        this.amount = amount;
        this.message = "Refund amount %f exceeds outstanding amount %f of payment transaction %d."
            .formatted(amount, outstandingAmount, paymentTransactionId);
    }
}
