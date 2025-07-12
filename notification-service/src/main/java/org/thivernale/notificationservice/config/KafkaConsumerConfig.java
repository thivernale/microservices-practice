package org.thivernale.notificationservice.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Define consumer configuration replacing {@link KafkaAutoConfiguration}
 */
@Configuration
@Slf4j
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootStrapServers;

    public Map<String, Object> consumerConfig() {
        HashMap<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "notificationGroup");
        props.put(JsonDeserializer.TYPE_MAPPINGS,
            "orderPlacedEvent:org.thivernale.notificationservice.notification.event.OrderPlacedEvent," +
                "paymentEvent:org.thivernale.notificationservice.notification.event.PaymentEvent");

        return props;
    }

    /**
     * Construct a consumer factory with the provided configuration
     */
    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfig());
    }

    /*public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, Object>> listenerContainerFactory(
        ConsumerFactory<String, ? super Object> consumerFactory
    ) {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }*/

    // ----------------protobuf----------------

    public Map<String, Object> protobufConsumerConfig() {
        HashMap<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "notificationGroup");
        return props;
    }

    @Bean
    public ConsumerFactory<String, Object> protobufConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(protobufConsumerConfig());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> protobufListenerContainerFactory(
        final ConsumerFactory<String, Object> protobufConsumerFactory
        /*, CommonErrorHandler commonErrorHandler*/
    ) {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(protobufConsumerFactory);
        //factory.setCommonErrorHandler(commonErrorHandler);
        return factory;
    }
}
