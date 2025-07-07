package org.thivernale.paymentservice.wallet.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.thivernale.paymentservice.wallet.model.PaymentTransactionCommand;
import org.thivernale.paymentservice.wallet.service.handler.PaymentTransactionCommandHandler;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentTransactionCommandListener {
    private final Map<PaymentTransactionCommand, PaymentTransactionCommandHandler> commandHandlers;

    @KafkaListener(
        /*topicPartitions = {
            @TopicPartition(
                topic = "${spring.kafka.topics[0].name:payment-command}",
                partitionOffsets = {
                    @PartitionOffset(
                        partition = "0", initialOffset = "0"
                    )
                },
                partitions = {"0"}
            )
        },*/
        topics = "${spring.kafka.topics[0].name:payment-command}",
        containerFactory = "listenerContainerFactory")
    public void handlePaymentTransactionCommand(ConsumerRecord<String, String> record) {
        PaymentTransactionCommand command = getPaymentTransactionCommand(record);
        PaymentTransactionCommandHandler handler = commandHandlers.get(command);

        if (handler == null) {
            throw new IllegalArgumentException("Unsupported payment transaction command " + command.toString() + " from record: " + record);
        }

        handler.process(record.key(), record.value());
    }

    private PaymentTransactionCommand getPaymentTransactionCommand(ConsumerRecord<String, String> record) {
        Header commandHeader = record.headers()
            .lastHeader("command");
        if (commandHeader != null && commandHeader.value() != null) {
            String commandString = new String(commandHeader.value());
            return PaymentTransactionCommand.fromString(commandString);
        }
        return PaymentTransactionCommand.UNKNOWN;
    }
}
