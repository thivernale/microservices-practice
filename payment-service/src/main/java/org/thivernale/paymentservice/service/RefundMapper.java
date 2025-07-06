package org.thivernale.paymentservice.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thivernale.paymentservice.dto.CancelPaymentTransactionRequest;
import org.thivernale.paymentservice.model.PaymentTransactionStatus;
import org.thivernale.paymentservice.model.Refund;

@Service
@RequiredArgsConstructor
public class RefundMapper {
    private final PaymentService paymentService;

    public Refund toRefund(@Valid CancelPaymentTransactionRequest request) {
        return Refund.builder()
            .payment(paymentService.findById(request.paymentTransactionId())
                .orElseThrow())
            .amount(request.amount())
            .currency(request.currency())
            .status(PaymentTransactionStatus.SUCCESS)
            .reason(request.reason())
            .build();
    }
}
