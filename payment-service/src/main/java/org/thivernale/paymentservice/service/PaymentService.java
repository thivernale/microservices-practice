package org.thivernale.paymentservice.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thivernale.paymentservice.dto.CreatePaymentTransactionRequest;
import org.thivernale.paymentservice.dto.PaymentRequest;
import org.thivernale.paymentservice.dto.PaymentResponse;
import org.thivernale.paymentservice.event.PaymentEvent;
import org.thivernale.paymentservice.model.Payment;
import org.thivernale.paymentservice.notification.NotificationProducer;
import org.thivernale.paymentservice.repository.PaymentRepository;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
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

    public PaymentResponse getPaymentResponse(Long id) {
        return findById(id)
            .map(paymentMapper::fromPayment)
            .orElseThrow(() -> new EntityNotFoundException(String.format("Payment with id %d not found", id)));
    }

    public Optional<Payment> findById(Long id) {
        return paymentRepository.findById(id);
    }

    public Payment save(CreatePaymentTransactionRequest request) {
        return paymentRepository.save(paymentMapper.toPayment(request));
    }

    public BigDecimal calculateOutstandingAmount(
        Payment payment,
        @NotNull(message = "Currency should be specified")
        String targetCurrency
    ) {
        return convert(payment.getAmount(), payment.getCurrency(), targetCurrency)
            .subtract(payment.getRefunds()
                .stream()
                .map(refund -> convert(refund.getAmount(), refund.getCurrency(), targetCurrency))
                .reduce(BigDecimal.ZERO, BigDecimal::add));
    }

    private BigDecimal convert(BigDecimal amount, String sourceCurrency, String targetCurrency) {
        // TODO currency conversion into target currency
        return amount;
    }
}
