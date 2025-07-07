package org.thivernale.paymentservice.wallet.notification;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.thivernale.paymentservice.wallet.model.PaymentTransactionCommand;

import java.util.concurrent.CompletableFuture;

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
        CompletableFuture<SendResult<String, String>> completableFuture = kafkaTemplate.send(paymentMessage);
        completableFuture.whenComplete((r, e) -> {
            if (e != null) {
                log.error(e.getMessage(), e);
            } else {
                log.info("Sent message {} with offset {}", message, r.getRecordMetadata()
                    .offset());
            }
        });
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
