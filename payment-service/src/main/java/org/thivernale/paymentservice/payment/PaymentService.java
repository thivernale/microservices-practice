package org.thivernale.paymentservice.payment;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thivernale.paymentservice.payment.dto.PaymentRequest;
import org.thivernale.paymentservice.payment.dto.PaymentResponse;
import org.thivernale.paymentservice.payment.event.PaymentEvent;
import org.thivernale.paymentservice.payment.notification.NotificationProducer;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final NotificationProducer notificationProducer;

    public Long createPayment(@Valid PaymentRequest paymentRequest) {
        Payment payment = paymentRepository.save(paymentMapper.toPayment(paymentRequest));

        notificationProducer.sendNotification(new PaymentEvent(
            paymentRequest.orderReference(),
            paymentRequest.amount(),
            paymentRequest.paymentMethod(),
            paymentRequest.customer()
                .firstName(),
            paymentRequest.customer()
                .lastName(),
            paymentRequest.customer()
                .email()
        ));
        return payment
            .getId();
    }

    public PaymentResponse getPayment(Long id) {
        return paymentRepository.findById(id)
            .map(paymentMapper::fromPayment)
            .orElseThrow(() -> new EntityNotFoundException(String.format("Payment with id %d not found", id)));
    }
}
