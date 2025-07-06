package org.thivernale.paymentservice.service;

import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thivernale.paymentservice.dto.CancelPaymentTransactionRequest;
import org.thivernale.paymentservice.dto.CreatePaymentTransactionRequest;
import org.thivernale.paymentservice.exception.PaymentTransactionValidationException;
import org.thivernale.paymentservice.exception.RefundValidationException;
import org.thivernale.paymentservice.model.BankAccount;
import org.thivernale.paymentservice.model.Payment;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentTransactionValidator {
    private final Validator validator;
    private final BankAccountService bankAccountService;
    private final PaymentService paymentService;

    public void validate(CreatePaymentTransactionRequest request) {
        List<String> errors = new ArrayList(validator.validate(request)
            .stream()
            .map(v -> v.getMessage())
            .toList());

        // validate source bank account and balance
        if (request.sourceBankAccountId() != null) {
            Optional<BankAccount> bankAccount = bankAccountService.findById(request.sourceBankAccountId());
            if (bankAccount.isEmpty()) {
                errors.add("Source bank account not found, id: " + request.sourceBankAccountId());
            } else if (bankAccount.get()
                .getBalance()
                .compareTo(request.amount()) < 0) {
                errors.add("Bank account balance not enough, id: " + request.sourceBankAccountId());
            }
        }
        // validate destination bank account
        if (request.destBankAccountId() != null && bankAccountService.findById(request.destBankAccountId())
            .isEmpty()) {
            errors.add("Destination bank account not found, id: " + request.destBankAccountId());
        }

        if (!errors.isEmpty()) {
            throw new PaymentTransactionValidationException(errors);
        }
    }

    public void validate(CancelPaymentTransactionRequest request) {
        List<String> errors = new ArrayList(validator.validate(request)
            .stream()
            .map(v -> v.getMessage())
            .toList());

        // validate payment transaction and outstanding amount
        if (request.paymentTransactionId() != null) {
            Optional<Payment> payment = paymentService.findByIdWithRefunds(request.paymentTransactionId());
            if (payment.isEmpty()) {
                errors.add("Payment not found, id: " + request.paymentTransactionId());
            } else if (request.amount()
                .compareTo(paymentService.calculateOutstandingAmount(payment.get(), request.currency())) > 0) {
                errors.add("Refund amount exceeds outstanding amount of payment, id: " + request.paymentTransactionId());
            }
        }

        if (!errors.isEmpty()) {
            throw new RefundValidationException(errors);
        }
    }
}
