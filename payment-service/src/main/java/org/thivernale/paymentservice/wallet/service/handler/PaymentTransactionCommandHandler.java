package org.thivernale.paymentservice.wallet.service.handler;

public interface PaymentTransactionCommandHandler {
    void process(String requestId, String message);
}
