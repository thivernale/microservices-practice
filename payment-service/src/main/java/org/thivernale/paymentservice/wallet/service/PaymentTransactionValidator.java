package org.thivernale.paymentservice.wallet.service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thivernale.paymentservice.wallet.dto.CancelPaymentTransactionRequest;
import org.thivernale.paymentservice.wallet.dto.CreatePaymentTransactionRequest;
import org.thivernale.paymentservice.wallet.exception.PaymentTransactionValidationException;
import org.thivernale.paymentservice.wallet.exception.RefundValidationException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentTransactionValidator {
    private final Validator validator;

    public void validate(CreatePaymentTransactionRequest request) {
        List<String> errors = new ArrayList<>(validator.validate(request)
            .stream()
            .map(ConstraintViolation::getMessage)
            .toList());

        if (!errors.isEmpty()) {
            throw new PaymentTransactionValidationException(errors);
        }
    }

    public void validate(CancelPaymentTransactionRequest request) {
        List<String> errors = new ArrayList<>(validator.validate(request)
            .stream()
            .map(ConstraintViolation::getMessage)
            .toList());

        if (!errors.isEmpty()) {
            throw new RefundValidationException(errors);
        }
    }
}
