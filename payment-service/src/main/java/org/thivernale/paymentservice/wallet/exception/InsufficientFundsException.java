package org.thivernale.paymentservice.wallet.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
public class InsufficientFundsException extends RuntimeException {
    private final String message;
    private final Long accountId;
    private final BigDecimal balance;
    private final BigDecimal amount;

    public InsufficientFundsException(Long accountId, BigDecimal balance, BigDecimal amount) {
        super();
        this.accountId = accountId;
        this.balance = balance;
        this.amount = amount;
        this.message = "Insufficient funds in account %d with balance %f to create payment transaction of amount %f.".formatted(accountId, balance, amount);
    }
}
