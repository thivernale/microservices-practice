package org.thivernale.paymentservice.wallet.service.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thivernale.paymentservice.util.JsonConverter;
import org.thivernale.paymentservice.wallet.dto.CancelPaymentTransactionRequest;
import org.thivernale.paymentservice.wallet.dto.CancelPaymentTransactionResponse;
import org.thivernale.paymentservice.wallet.model.PaymentTransactionCommand;
import org.thivernale.paymentservice.wallet.model.Refund;
import org.thivernale.paymentservice.wallet.notification.PaymentTransactionProducer;
import org.thivernale.paymentservice.wallet.service.PaymentTransactionValidator;
import org.thivernale.paymentservice.wallet.service.RefundService;

@Service
@RequiredArgsConstructor
@Slf4j
public class CancelPaymentTransactionHandler implements PaymentTransactionCommandHandler {
    private final PaymentTransactionValidator validator;
    private final RefundService refundService;
    private final PaymentTransactionProducer paymentTransactionProducer;
    private final JsonConverter jsonConverter;

    @Override
    public void process(String requestId, String message) {
        CancelPaymentTransactionRequest request = jsonConverter.toObject(message, CancelPaymentTransactionRequest.class);

        validator.validate(request);

        Refund refund = refundService.create(request);

        CancelPaymentTransactionResponse response = new CancelPaymentTransactionResponse(
            refund.getId(),
            refund.getStatus(),
            null,
            refund.getCreatedAt()
        );

        // TODO send response on error - maybe in global error handler
        paymentTransactionProducer.sendCommandResult(
            paymentTransactionProducer.getResultTopic(),
            requestId,
            jsonConverter.toString(response),
            PaymentTransactionCommand.REFUND
        );
    }
}
