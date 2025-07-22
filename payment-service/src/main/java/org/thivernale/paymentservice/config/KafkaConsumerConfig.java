package org.thivernale.paymentservice.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.MessageListenerContainer;

import java.util.HashMap;
import java.util.Map;

/**
 * Define consumer configuration replacing {@link KafkaAutoConfiguration}
 */
@Configuration
@Slf4j
public class KafkaConsumerConfig {

    public Map<String, Object> consumerConfig(String bootStrapServers) {
        HashMap<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "paymentGroup");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        return props;
    }

    /**
     * Construct a consumer factory with the provided configuration
     */
    @Bean
    public ConsumerFactory<String, String> consumerFactory(
        @Value("${spring.kafka.bootstrap-servers}") String bootStrapServers) {
        return new DefaultKafkaConsumerFactory<>(consumerConfig(bootStrapServers));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> listenerContainerFactory(
        final ConsumerFactory<String, String> consumerFactory, CommonErrorHandler commonErrorHandler
    ) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setCommonErrorHandler(commonErrorHandler);
        return factory;
    }

    @Bean
    CommonErrorHandler commonErrorHandler() {
        return new CommonErrorHandler() {
            @Override
            public boolean handleOne(Exception thrownException, ConsumerRecord<?, ?> record, Consumer<?, ?> consumer, MessageListenerContainer container) {
//                return CommonErrorHandler.super.handleOne(thrownException, record, consumer, container);
                handle(thrownException, consumer);
                return true;
            }

            @Override
            public void handleOtherException(Exception thrownException, Consumer<?, ?> consumer, MessageListenerContainer container, boolean batchListener) {
                CommonErrorHandler.super.handleOtherException(thrownException, consumer, container, batchListener);
            }

            void handle(Exception exception, Consumer<?, ?> consumer) {
                //
                //CompletableFuture.runAsync(() -> {});
                log.error(exception.getMessage());
                if (exception.getCause() != null) {
                    log.error(exception.getCause()
                        .getMessage());
                }
            }
        };
    }
}
