package org.thivernale.orderservice.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.thivernale.orderservice.event.OrderPlacedEvent;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationProducer {
    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    public void sendNotification(OrderPlacedEvent request) {
        log.info("Sending notification request: {}", request);

        kafkaTemplate.send(MessageBuilder.withPayload(request)
            .setHeader(KafkaHeaders.TOPIC, "orderTopic")
            .build());
    }

    public void sendNotification(OrderPlacedEvent request, String key, String topic) {
        log.info("Sending notification request: {} to topic {}", request, topic);

        kafkaTemplate.send(MessageBuilder.withPayload(request)
            .setHeader(KafkaHeaders.TOPIC, topic)
            .setHeader(KafkaHeaders.KEY, key)
            .build());
    }
}
