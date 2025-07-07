package org.thivernale.paymentservice.wallet.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thivernale.paymentservice.wallet.dto.CancelPaymentTransactionRequest;
import org.thivernale.paymentservice.wallet.model.PaymentTransactionStatus;
import org.thivernale.paymentservice.wallet.model.Refund;

@Service
@RequiredArgsConstructor
public class RefundMapper {
    private final PaymentTransactionService paymentTransactionService;

    public Refund toRefund(@Valid CancelPaymentTransactionRequest request) {
        return Refund.builder()
            .paymentTransaction(paymentTransactionService.findById(request.paymentTransactionId())
                .orElseThrow())
            .amount(request.amount())
            .currency(request.currency())
            .status(PaymentTransactionStatus.SUCCESS)
            .reason(request.reason())
            .build();
    }
}
