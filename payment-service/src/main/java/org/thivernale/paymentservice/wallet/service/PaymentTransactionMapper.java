package org.thivernale.paymentservice.wallet.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thivernale.paymentservice.wallet.dto.CreatePaymentTransactionRequest;
import org.thivernale.paymentservice.wallet.model.PaymentTransaction;
import org.thivernale.paymentservice.wallet.model.PaymentTransactionStatus;

@Service
@RequiredArgsConstructor
public class PaymentTransactionMapper {
    private final BankAccountService bankAccountService;

    public PaymentTransaction toPaymentTransaction(@Valid CreatePaymentTransactionRequest paymentRequest) {
        return PaymentTransaction.builder()
            .amount(paymentRequest.amount())
            .currency(paymentRequest.currency())
            .sourceBankAccount(bankAccountService.findById(paymentRequest.sourceBankAccountId())
                .orElseThrow())
            .destBankAccount(
                paymentRequest.destBankAccountId() == null ?
                    null :
                    bankAccountService.findById(paymentRequest.destBankAccountId())
                        .orElseThrow())
            .status(PaymentTransactionStatus.SUCCESS)
            .note(paymentRequest.note())
            .build();
    }
}
