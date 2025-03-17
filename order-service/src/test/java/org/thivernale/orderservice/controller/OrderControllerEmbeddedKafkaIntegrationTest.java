package org.thivernale.orderservice.controller;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.thivernale.orderservice.TestOrderServiceApplication;
import org.thivernale.orderservice.dto.InventoryResponse;
import org.thivernale.orderservice.dto.OrderRequest;
import org.thivernale.orderservice.dto.OrderResponse;

import java.net.URI;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.thivernale.orderservice.TestDataUtil.*;
import static org.thivernale.orderservice.controller.OrderStubs.*;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
        "spring.cloud.discovery.client.simple.instances.inventory-service[0].uri=http://localhost:1032",
        "spring.cloud.discovery.client.simple.instances.customer-service[0].uri=http://localhost:1032",
        "spring.cloud.discovery.client.simple.instances.payment-service[0].uri=http://localhost:1032",
    }
)
@ContextConfiguration(classes = {TestOrderServiceApplication.class, WireMockConfig.class})
@EmbeddedKafka(
    partitions = 1,
    brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"},
    kraft = true,
    topics = {"orderTopic", "codeTopic"}
)
@DirtiesContext
public class OrderControllerEmbeddedKafkaIntegrationTest {
    @Autowired
    private WireMockServer wireMockServer;
    @Autowired
    private TestRestTemplate restTemplate;
    @LocalServerPort
    private int port;

    @Test
    void shouldCheckAvailability() {

        List<InventoryResponse> inventoryResponseList = getInventoryResponseList();
        setupMockGetInventoryAvailability(wireMockServer, getInventoryRequestMap(), inventoryResponseList);

        ResponseEntity<List<InventoryResponse>> responseEntity = restTemplate.exchange(
            uri("/api/order/check-availability"),
            POST,
            new HttpEntity<>(createOrderRequest()),
            new ParameterizedTypeReference<>() {
            });

        assertThat(responseEntity.getStatusCode())
            .isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(inventoryResponseList);
    }

    @Test
    public void whenCustomerNotFound_thenCreateOrderShouldFail() {
        setupMockGetCustomerNotFound(wireMockServer, "any-customer-id");

        ResponseEntity<?> responseEntity = restTemplate.exchange(
            uri("/api/order"),
            POST,
            new HttpEntity<>(createOrderRequest()),
            Object.class
        );

        assertThat(responseEntity.getStatusCode())
            .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    public void whenOrderInputValid_thenCreateOrderShouldSucceed() {
        setupMockGetCustomer(wireMockServer, "67bc84af1e856a7494958bd3", createCustomer());
        setupMockGetInventoryAvailability(wireMockServer, getInventoryRequestMap(), getInventoryResponseList());
        setupMockCreatePayment(wireMockServer);

        OrderRequest orderRequest = createOrderRequest();
        ResponseEntity<Long> responseEntity = restTemplate.exchange(
            uri("/api/order"),
            POST,
            new HttpEntity<>(orderRequest),
            Long.class
        );
        Long orderId = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode())
            .isEqualTo(HttpStatus.CREATED);
        assertThat(orderId).isNotNull();

        ResponseEntity<OrderResponse> responseEntityGet = restTemplate.exchange(
            uri("/api/order/{orderId}", orderId),
            GET,
            null,
            OrderResponse.class
        );

        assertThat(responseEntityGet.getStatusCode())
            .isEqualTo(HttpStatus.OK);

        OrderResponse orderResponse = responseEntityGet.getBody();
        assertThat(orderResponse).isNotNull();
        assertThat(orderResponse.getId()).isEqualTo(orderId);
        assertThat(orderResponse)
            .usingRecursiveComparison()
            .ignoringCollectionOrder()
            .ignoringFields("id", "reference", "items.id")
            .isEqualTo(orderRequest);
    }

    private URI uri(String path, Object... uriVariables) {
        return restTemplate.getRestTemplate()
            .getUriTemplateHandler()
            .expand(path, uriVariables);
    }
}
