package org.thivernale.paymentservice.wallet.service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thivernale.paymentservice.wallet.dto.CancelPaymentTransactionRequest;
import org.thivernale.paymentservice.wallet.dto.CreatePaymentTransactionRequest;
import org.thivernale.paymentservice.wallet.exception.PaymentTransactionValidationException;
import org.thivernale.paymentservice.wallet.exception.RefundValidationException;
import org.thivernale.paymentservice.wallet.model.BankAccount;
import org.thivernale.paymentservice.wallet.model.PaymentTransaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentTransactionValidator {
    private final Validator validator;
    private final BankAccountService bankAccountService;
    private final PaymentTransactionService paymentTransactionService;

    public void validate(CreatePaymentTransactionRequest request) {
        List<String> errors = new ArrayList<>(validator.validate(request)
            .stream()
            .map(ConstraintViolation::getMessage)
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
        List<String> errors = new ArrayList<>(validator.validate(request)
            .stream()
            .map(ConstraintViolation::getMessage)
            .toList());

        // validate payment transaction and outstanding amount
        if (request.paymentTransactionId() != null) {
            Optional<PaymentTransaction> paymentTransaction = paymentTransactionService.findByIdWithRefunds(request.paymentTransactionId());
            if (paymentTransaction.isEmpty()) {
                errors.add("Payment transaction not found, id: " + request.paymentTransactionId());
            } else if (request.amount()
                .compareTo(paymentTransactionService.calculateOutstandingAmount(paymentTransaction.get(), request.currency())) > 0) {
                errors.add("Refund amount exceeds outstanding amount of payment transaction, id: " + request.paymentTransactionId());
            }
        }

        if (!errors.isEmpty()) {
            throw new RefundValidationException(errors);
        }
    }
}
