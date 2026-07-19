package org.thivernale.paymentservice.web.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.thivernale.paymentservice.payment.PaymentService;
import org.thivernale.paymentservice.payment.dto.PaymentRequest;
import org.thivernale.paymentservice.payment.dto.PaymentResponse;
import org.thivernale.paymentservice.util.JsonConverter;
import org.thivernale.paymentservice.wallet.dto.CancelPaymentTransactionRequest;
import org.thivernale.paymentservice.wallet.dto.CreatePaymentTransactionRequest;
import org.thivernale.paymentservice.wallet.model.PaymentTransactionCommand;
import org.thivernale.paymentservice.wallet.notification.PaymentTransactionProducer;

import java.util.UUID;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {
    private final PaymentService paymentService;
    private final PaymentTransactionProducer paymentTransactionProducer;
    private final JsonConverter jsonConverter;

    private final String requestId = UUID.randomUUID()
        .toString();
    @Value("${spring.kafka.topics[0].name:payment-command}")
    private String topic;

    @PostMapping
    public ResponseEntity<Long> createPayment(@RequestBody @Valid PaymentRequest paymentRequest) {
        return ResponseEntity.ok(paymentService.createPayment(paymentRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPayment(@PathVariable("id") Long id) {
        return ResponseEntity.ok(paymentService.getPayment(id));
    }

    @PostMapping("/create-payment")
    public void createPayment(@Valid @NotNull @RequestBody CreatePaymentTransactionRequest request) {
        paymentTransactionProducer.sendCommandResult(
            topic, requestId, jsonConverter.toString(request), PaymentTransactionCommand.CREATE
        );
    }

    @PostMapping("/cancel-payment")
    public void cancelPayment(@Valid @NotNull @RequestBody CancelPaymentTransactionRequest request) {
        paymentTransactionProducer.sendCommandResult(
            topic, requestId, jsonConverter.toString(request), PaymentTransactionCommand.REFUND
        );
    }
}
