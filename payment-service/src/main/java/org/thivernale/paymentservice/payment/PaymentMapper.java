package org.thivernale.paymentservice.payment;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thivernale.paymentservice.payment.dto.PaymentRequest;
import org.thivernale.paymentservice.payment.dto.PaymentResponse;

@Service
@RequiredArgsConstructor
class PaymentMapper {
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
