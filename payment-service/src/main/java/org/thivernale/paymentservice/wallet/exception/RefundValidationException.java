package org.thivernale.paymentservice.wallet.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class RefundValidationException extends RuntimeException {
    private List<String> errors;
}
