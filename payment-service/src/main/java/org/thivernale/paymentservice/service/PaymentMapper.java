package org.thivernale.paymentservice.service;

import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.thivernale.paymentservice.dto.PaymentRequest;
import org.thivernale.paymentservice.dto.PaymentResponse;
import org.thivernale.paymentservice.model.Payment;

@Service
public class PaymentMapper {
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
}
