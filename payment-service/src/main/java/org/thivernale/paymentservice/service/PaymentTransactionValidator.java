package org.thivernale.paymentservice.service;

import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thivernale.paymentservice.dto.CreatePaymentTransactionRequest;
import org.thivernale.paymentservice.exception.PaymentTransactionValidationException;
import org.thivernale.paymentservice.model.BankAccount;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentTransactionValidator {
    private final Validator validator;
    private final BankAccountService bankAccountService;

    public void validate(CreatePaymentTransactionRequest request) {
        List<String> errors = validator.validate(request)
            .stream()
            .map(v -> v.getMessage())
            .toList();

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
}
