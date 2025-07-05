package org.thivernale.paymentservice.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thivernale.paymentservice.dto.CreatePaymentTransactionRequest;
import org.thivernale.paymentservice.dto.PaymentRequest;
import org.thivernale.paymentservice.dto.PaymentResponse;
import org.thivernale.paymentservice.model.Payment;
import org.thivernale.paymentservice.model.PaymentTransactionStatus;

@Service
@RequiredArgsConstructor
public class PaymentMapper {
    private final BankAccountService bankAccountService;

    public PaymentResponse fromPayment(Payment payment) {
        return new PaymentResponse(
            payment.getId(),
            payment.getAmount(),
            payment.getPaymentMethod(),
            payment.getOrderId()
        );
    }

    public Payment toPayment(@Valid PaymentRequest paymentRequest) {
        return Payment.builder()
            .id(paymentRequest.id())
            .amount(paymentRequest.amount())
            .paymentMethod(paymentRequest.paymentMethod())
            .orderId(paymentRequest.orderId())
            .build();
    }

    public Payment toPayment(@Valid CreatePaymentTransactionRequest paymentRequest) {
        return Payment.builder()
            .amount(paymentRequest.amount())
            .currency(paymentRequest.currency())
            .paymentMethod(paymentRequest.paymentMethod())
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
