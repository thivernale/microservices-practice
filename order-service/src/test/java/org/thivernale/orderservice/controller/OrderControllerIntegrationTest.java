package org.thivernale.orderservice.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.ConfluentKafkaContainer;
import org.testcontainers.shaded.org.awaitility.Awaitility;
import org.testcontainers.utility.DockerImageName;
import org.thivernale.orderservice.TestOrderServiceApplication;
import org.thivernale.orderservice.dto.OrderResponse;
import org.thivernale.orderservice.event.OrderPlacedEvent;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import static java.time.Duration.ofMillis;
import static java.time.Duration.ofSeconds;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ContextConfiguration(classes = {TestOrderServiceApplication.class, WireMockConfig.class, KafkaConsumerConfig.class})
class OrderControllerIntegrationTest {
    @Container
    static ConfluentKafkaContainer kafkaContainer = new ConfluentKafkaContainer(
        DockerImageName.parse("confluentinc/cp-kafka:7.4.0"));

    @Autowired
    private MySQLContainer<?> mysqlContainer;
    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private CodeKafkaConsumer codeKafkaConsumer;

    @DynamicPropertySource
    static void setKafkaContainer(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
    }

    @Test
    public void shouldBeRunning() {
        assertThat(mysqlContainer.isCreated()).isTrue();
        assertThat(mysqlContainer.isRunning()).isTrue();
        assertThat(kafkaContainer.isCreated()).isTrue();
        assertThat(kafkaContainer.isRunning()).isTrue();
    }

    @Test
    public void shouldGetOrders() {
        ResponseEntity<List<OrderResponse>> responseEntity =
            restTemplate.exchange(uri("/api/order"), GET, null, new ParameterizedTypeReference<>() {
            });

        assertThat(responseEntity.getStatusCode())
            .isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(Collections.emptyList());
    }

    @Test
    public void shouldSendTestEvent() {
        ResponseEntity<Void> responseEntity = restTemplate.exchange(
            uri("/api/order/send-test-event"),
            POST,
            null,
            Void.class);

        assertThat(responseEntity.getStatusCode())
            .isEqualTo(HttpStatus.ACCEPTED);

        Awaitility.await()
            .atMost(ofSeconds(1))
            .pollInterval(ofMillis(20))
            .untilAsserted(() ->
                assertThat(codeKafkaConsumer.getOrderPlacedEvent())
                    .describedAs("OrderPlacedEvent consumed")
                    .isNotNull()
                    .isInstanceOf(OrderPlacedEvent.class)
            );
    }

    private URI uri(String path) {
        return restTemplate.getRestTemplate()
            .getUriTemplateHandler()
            .expand(path);
    }
}
