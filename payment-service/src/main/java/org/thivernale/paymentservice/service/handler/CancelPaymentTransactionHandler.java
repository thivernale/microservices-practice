package org.thivernale.paymentservice.service.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CancelPaymentTransactionHandler implements PaymentTransactionCommandHandler {
    @Override
    public void process(String requestId, String message) {

    }
}
