package org.thivernale.notificationservice;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.KafkaListener;
import org.thivernale.notificationservice.event.OrderPlacedEvent;

@SpringBootApplication
@Slf4j
public class NotificationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }

    @KafkaListener(
        topics = {"notificationTopic", "codeTopic"}
        //  kafka-consumer-groups --bootstrap-server localhost:9092 --group notificationId --topic codeTopic
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
    public void handleNotification(ConsumerRecord<String, OrderPlacedEvent> record) {
        // send out an email notification
        var orderPlacedEvent = record.value();
        log.info("Received Notification for Order - {} with key {}", orderPlacedEvent.getOrderNumber(), record.key());
    }
}
