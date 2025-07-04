package org.thivernale.paymentservice.config;

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
import org.thivernale.paymentservice.event.PaymentEvent;

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
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "false");

        return props;
    }

    /**
     * Construct a producer factory with the provided configuration
     */
    @Bean
    public ProducerFactory<String, String> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfig());
    }

    /**
     * Construct a Kafka template with provided producer factory
     */
    @Bean
    public KafkaTemplate<String, String> kafkaTemplate(ProducerFactory<String, String> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    // ------------------------PaymentEvent----------------------

    @Bean
    public ProducerFactory<String, PaymentEvent> paymentProducerFactory() {
        HashMap<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(JsonSerializer.TYPE_MAPPINGS, "paymentEvent:org.thivernale.paymentservice.event.PaymentEvent");

        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    KafkaTemplate<String, PaymentEvent> paymentKafkaTemplate(ProducerFactory<String, PaymentEvent> paymentProducerFactory) {
        return new KafkaTemplate<>(paymentProducerFactory);
    }
}
