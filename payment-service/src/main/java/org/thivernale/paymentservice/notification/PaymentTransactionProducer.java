package org.thivernale.paymentservice.notification;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.thivernale.paymentservice.model.PaymentTransactionCommand;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentTransactionProducer {
    private static final String PAYMENT_TRANSACTION_COMMAND_TYPE_HEADER = "command";
    private final KafkaTemplate<String, String> kafkaTemplate;
    @Getter
    @Value("${spring.kafka.topics[1].name:payment-command-result}")
    private String resultTopic;

    public void sendCommandResult(String topic, String requestId, String message, PaymentTransactionCommand command) {
        Message<String> paymentMessage = buildMessage(topic, requestId, message, command);
        kafkaTemplate.send(paymentMessage);
        log.info("Successfully sent payment command result: {}", paymentMessage);
    }

    private Message<String> buildMessage(String topic, String requestId, String message, PaymentTransactionCommand command) {
        return MessageBuilder.withPayload(message)
            .setHeader(KafkaHeaders.KEY, requestId)
            .setHeader(KafkaHeaders.TOPIC, topic)
            .setHeader(PAYMENT_TRANSACTION_COMMAND_TYPE_HEADER, command.toString())
            .build();
    }
}
