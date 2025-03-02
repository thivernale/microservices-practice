package org.thivernale.orderservice.controller;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.thivernale.orderservice.event.OrderPlacedEvent;

@Slf4j
@Component
@Getter
@Setter
@ConditionalOnBean(KafkaConsumerConfig.class)
public class CodeKafkaConsumer {
    private OrderPlacedEvent orderPlacedEvent;

    @KafkaListener(topics = {"codeTopic"}, containerFactory = "listenerContainerFactory")
    public void handleCodeNotification(ConsumerRecord<String, OrderPlacedEvent> record) {

        log.info("Received Notification for Order - {} with key {}", record.value(), record.key());

        this.orderPlacedEvent = record.value();
    }
}
