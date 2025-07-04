package org.thivernale.paymentservice.model;

import lombok.Getter;

@Getter
public enum PaymentTransactionStatus {
    PROCESSING,
    SUCCESS,
    FAILED;

    public static PaymentTransactionStatus fromValue(String value) {
        for (PaymentTransactionStatus status : PaymentTransactionStatus.values()) {
            if (status.toString()
                .equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid PaymentTransactionStatus value: " + value);
    }

}
