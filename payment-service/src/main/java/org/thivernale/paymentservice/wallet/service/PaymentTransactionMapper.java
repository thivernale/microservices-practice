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
    private final CurrencyAccountService currencyAccountService;

    public PaymentTransaction toPaymentTransaction(@Valid CreatePaymentTransactionRequest paymentRequest) {
        return PaymentTransaction.builder()
            .amount(paymentRequest.amount())
            .source(currencyAccountService.findById(paymentRequest.sourceCurrencyAccountId())
                .orElseThrow())
            .destination(
                paymentRequest.destCurrencyAccountId() == null ?
                    null :
                    currencyAccountService.findById(paymentRequest.destCurrencyAccountId())
                        .orElseThrow())
            .status(PaymentTransactionStatus.SUCCESS)
            .note(paymentRequest.note())
            .build();
    }
}
