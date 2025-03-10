package org.thivernale.notificationservice.config;

import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.aop.ObservedAspect;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;

@Configuration
@RequiredArgsConstructor
public class ObservabilityConfig {

    private final ConcurrentKafkaListenerContainerFactory<?, ?> kafkaListenerContainerFactory;

    @PostConstruct
    public void setObservedAspect() {
        kafkaListenerContainerFactory.getContainerProperties()
            .setObservationEnabled(true);
    }

    @Bean
    ObservedAspect observedAspect(ObservationRegistry observationRegistry) {
        return new ObservedAspect(observationRegistry);
    }
}
