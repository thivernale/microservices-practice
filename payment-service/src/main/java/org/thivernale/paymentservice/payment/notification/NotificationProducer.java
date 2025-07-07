package org.thivernale.paymentservice.payment.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.thivernale.paymentservice.payment.event.PaymentEvent;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationProducer {
    private final KafkaTemplate<String, PaymentEvent> kafkaTemplate;

    public void sendNotification(PaymentEvent request) {
        log.info("Sending notification request: {}", request);
        Message<PaymentEvent> paymentMessage = MessageBuilder.withPayload(request)
            .setHeader(KafkaHeaders.TOPIC, "paymentTopic")
            .build();
        kafkaTemplate.send(paymentMessage);
    }
}
