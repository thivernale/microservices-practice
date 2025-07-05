package org.thivernale.paymentservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thivernale.paymentservice.model.PaymentTransactionCommand;
import org.thivernale.paymentservice.service.handler.CancelPaymentTransactionHandler;
import org.thivernale.paymentservice.service.handler.CreatePaymentTransactionHandler;
import org.thivernale.paymentservice.service.handler.PaymentTransactionCommandHandler;

import java.util.HashMap;
import java.util.Map;

import static org.thivernale.paymentservice.model.PaymentTransactionCommand.CREATE;
import static org.thivernale.paymentservice.model.PaymentTransactionCommand.REFUND;

@Configuration
public class PaymentTransactionCommandConfig {
    @Bean
    public Map<PaymentTransactionCommand, PaymentTransactionCommandHandler> paymentTransactionCommandHandlers(
        CreatePaymentTransactionHandler createHandler,
        CancelPaymentTransactionHandler cancelHandler
    ) {
        Map<PaymentTransactionCommand, PaymentTransactionCommandHandler> commandHandlers = new HashMap<>();
        commandHandlers.put(CREATE, createHandler);
        commandHandlers.put(REFUND, cancelHandler);
        return commandHandlers;
    }
}
