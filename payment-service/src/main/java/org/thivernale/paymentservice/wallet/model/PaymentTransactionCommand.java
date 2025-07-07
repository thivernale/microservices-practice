package org.thivernale.paymentservice.wallet.model;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public enum PaymentTransactionCommand {
    CREATE, REFUND, UNKNOWN;

    public static PaymentTransactionCommand fromString(String value) {
        for (PaymentTransactionCommand command : PaymentTransactionCommand.values()) {
            if (command.name()
                .equalsIgnoreCase(value)) {
                return command;
            }
        }
        return PaymentTransactionCommand.UNKNOWN;
    }
}
