package org.thivernale.paymentservice.exception;

import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class RefundValidationException extends RuntimeException {
    private List<String> errors;
}
