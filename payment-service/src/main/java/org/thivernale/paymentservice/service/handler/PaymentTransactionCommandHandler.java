package org.thivernale.paymentservice.service.handler;

public interface PaymentTransactionCommandHandler {
    void process(String requestId, String message);
}
