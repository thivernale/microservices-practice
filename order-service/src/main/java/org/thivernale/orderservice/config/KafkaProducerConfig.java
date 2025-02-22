package org.thivernale.orderservice.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.thivernale.orderservice.event.OrderPlacedEvent;

import java.util.HashMap;
import java.util.Map;


/**
 * Define producer configuration replacing {@link KafkaAutoConfiguration}
 */
@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootStrapServers;

    public Map<String, Object> producerConfig() {
        HashMap<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(JsonSerializer.TYPE_MAPPINGS, "orderPlacedEvent:org.thivernale.orderservice.event.OrderPlacedEvent");

        return props;
    }

    /**
     * Construct a producer factory with the provided configuration
     */
    @Bean
    public ProducerFactory<String, OrderPlacedEvent> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfig());
    }

    /**
     * Construct a Kafka template with provided producer factory
     */
    @Bean
    public KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate(ProducerFactory<String, OrderPlacedEvent> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }
}
