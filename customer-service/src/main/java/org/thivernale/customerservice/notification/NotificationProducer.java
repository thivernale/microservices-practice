package org.thivernale.customerservice.notification;

import customer.events.CustomerEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.thivernale.customerservice.model.Customer;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationProducer {
    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    @Value("${spring.kafka.template.default-topic:}")
    private String customerTopic;

    public void sendNotification(Customer customer) {
        CustomerEvent event = CustomerEvent.newBuilder()
            .setClientId(customer.getId())
            .setName(customer.getFirstName() + " " + customer.getLastName())
            .setEmail(customer.getEmail())
            .setEventType("CUSTOMER_CREATED")
            .build();

        try {
            Message<byte[]> customerMessage = MessageBuilder.withPayload(event.toByteArray())
                .setHeader(KafkaHeaders.TOPIC, customerTopic)
                .build();
            kafkaTemplate.send(customerMessage);

            log.info("Sending notification request in protobuf format: {}", event);
        } catch (Exception e) {
            log.error("Error sending CustomerEvent: {}", event);
        }
    }
}
