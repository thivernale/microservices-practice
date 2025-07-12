package org.thivernale.notificationservice.notification;

import com.google.protobuf.InvalidProtocolBufferException;
import customer.events.CustomerEvent;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.thivernale.notificationservice.email.EmailService;
import org.thivernale.notificationservice.notification.event.OrderPlacedEvent;
import org.thivernale.notificationservice.notification.event.PaymentEvent;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.thivernale.notificationservice.notification.NotificationType.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {

    private final NotificationRepository notificationRepository;
    private final EmailService emailService;

    @KafkaListener(
        topics = {"codeTopic"}
        //  kafka-consumer-groups --bootstrap-server localhost:9092 --group notificationGroup --topic codeTopic
        //  --delete-offsets
        /*topicPartitions = {
            @TopicPartition(
                topic = "codeTopic",
                partitionOffsets = @PartitionOffset(
                    partition = "0",
                    initialOffset = "0",
                    relativeToCurrent = "true"
                )
            )
        }*/
        )
    public void handleCodeNotification(ConsumerRecord<String, OrderPlacedEvent> record) {
        // send out an email notification
        var orderPlacedEvent = record.value();
        log.info("Received Notification for Order - {} with key {}", orderPlacedEvent.orderReference(), record.key());
    }

    @KafkaListener(topics = {"orderTopic"})
    public void handleOrderPlacedEvent(OrderPlacedEvent orderPlacedEvent) throws MessagingException {

        log.info("Consuming message from orderTopic: {}", orderPlacedEvent);

        notificationRepository.save(Notification.builder()
            .notificationType(ORDER_CONFIRMATION)
            .notificationTime(LocalDateTime.now())
            .orderPlacedEvent(orderPlacedEvent)
            .build());

        emailService.sendOrderConfirmationEmail(
            orderPlacedEvent.customer()
                .email(),
            "%s %s".formatted(
                orderPlacedEvent.customer()
                    .firstName(),
                orderPlacedEvent.customer()
                    .lastName()
            ),
            orderPlacedEvent.orderReference(),
            orderPlacedEvent.totalAmount(),
            orderPlacedEvent.products()
        );
    }

    @KafkaListener(topics = {"paymentTopic"})
    public void handlePaymentNotification(PaymentEvent paymentEvent) throws MessagingException {

        log.info("Consuming message from paymentTopic: {}", paymentEvent);

        notificationRepository.save(Notification.builder()
            .notificationType(PAYMENT_CONFIRMATION)
            .notificationTime(LocalDateTime.now())
            .paymentEvent(paymentEvent)
            .build());

        emailService.sendPaymentSuccessEmail(
            paymentEvent.customerEmail(),
            "%s %s".formatted(paymentEvent.customerFirstName(), paymentEvent.customerLastName()),
            paymentEvent.orderReference(),
            paymentEvent.amount()
        );
    }

    @KafkaListener(
        topics = {"customerTopic"},
        containerFactory = "protobufListenerContainerFactory"
    )
    public void handleCustomerNotification(byte[] event) {
        try {
            CustomerEvent customerEvent = CustomerEvent.parseFrom(event);

            log.info("Consuming message from customerTopic in protobuf format: {}", customerEvent);

            notificationRepository.save(Notification.builder()
                .notificationType(CUSTOMER_CREATED)
                .notificationTime(LocalDateTime.now())
                .paymentEvent(new PaymentEvent(
                    customerEvent.getEventType(),
                    BigDecimal.ZERO,
                    null,
                    customerEvent.getClientId(),
                    customerEvent.getName(),
                    customerEvent.getEmail()
                ))
                .build());
        } catch (InvalidProtocolBufferException e) {
            log.error("Error parsing protobuf event {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
