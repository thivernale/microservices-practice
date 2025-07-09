package org.thivernale.paymentservice.wallet.service.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thivernale.paymentservice.util.JsonConverter;
import org.thivernale.paymentservice.wallet.dto.CreatePaymentTransactionRequest;
import org.thivernale.paymentservice.wallet.dto.CreatePaymentTransactionResponse;
import org.thivernale.paymentservice.wallet.model.PaymentTransaction;
import org.thivernale.paymentservice.wallet.model.PaymentTransactionCommand;
import org.thivernale.paymentservice.wallet.notification.PaymentTransactionProducer;
import org.thivernale.paymentservice.wallet.service.PaymentTransactionService;
import org.thivernale.paymentservice.wallet.service.PaymentTransactionValidator;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreatePaymentTransactionHandler implements PaymentTransactionCommandHandler {
    private final PaymentTransactionValidator validator;
    private final PaymentTransactionService paymentTransactionService;
    private final PaymentTransactionProducer paymentTransactionProducer;
    private final JsonConverter jsonConverter;

    @Override
    public void process(String requestId, String message) {
        CreatePaymentTransactionRequest request = jsonConverter.toObject(message, CreatePaymentTransactionRequest.class);

        validator.validate(request);

        PaymentTransaction paymentTransaction = paymentTransactionService.create(request);

        CreatePaymentTransactionResponse response = new CreatePaymentTransactionResponse(
            paymentTransaction.getId(),
            paymentTransaction.getStatus(),
            null,
            paymentTransaction.getCreatedAt()
        );

        // TODO send response on error - maybe in global error handler
        paymentTransactionProducer.sendCommandResult(
            paymentTransactionProducer.getResultTopic(),
            requestId,
            jsonConverter.toString(response),
            PaymentTransactionCommand.CREATE
        );
    }
}
