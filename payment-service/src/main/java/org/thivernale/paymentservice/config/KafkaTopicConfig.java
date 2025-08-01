package org.thivernale.paymentservice.config;

import lombok.Data;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "spring.kafka")
public class KafkaTopicConfig {
    private List<TopicConfig> topics = new ArrayList<>();

    @Bean
    public NewTopic paymentTopic() {
        return TopicBuilder.name("paymentTopic")
            .build();
    }

    // KafkaAdmin bean will automatically create topics for all beans of type NewTopic
    @Bean
    public KafkaAdmin kafkaAdmin(@Value("${spring.kafka.bootstrap-servers}") String bootStrapServers) {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrapServers);
        configs.put("offsets.topic.replication.factor", "1");
        configs.put("transaction.state.log.replication.factor", "1");
        configs.put("transaction.state.log.min.isr", "1");

        return new KafkaAdmin(configs);
    }

    @Bean
    public List<NewTopic> createTopics() {
        // create topics out of configurations
        return topics.stream()
            .peek(topic -> System.out.println("Creating topic " + topic))
            .map(config -> TopicBuilder.name(config.name)
                .partitions(config.partitions)
                .replicas(config.replicationFactor)
                .build())
            .toList();
    }

    @Data
    public static class TopicConfig {
        private String name;
        private int partitions;
        private int replicationFactor;
    }
}
