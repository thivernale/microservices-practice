package org.thivernale.inventoryservice.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.thivernale.inventoryservice.TestInventoryServiceApplication;
import org.thivernale.inventoryservice.dto.InventoryResponse;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.POST;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = {TestInventoryServiceApplication.class})
@Testcontainers
class InventoryControllerIntegrationTest {

    @Autowired
    private MySQLContainer<?> mysqlContainer;
    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private JdbcClient jdbcClient;

    @Test
    public void shouldBeRunning() {
        assertThat(mysqlContainer.isCreated()).isTrue();
        assertThat(mysqlContainer.isRunning()).isTrue();
    }

    @Test
    @Sql(scripts = {"classpath:data/test_inventory_data.sql"})
    void whenInventoryIsInStock_thenReturnInventoryResponse() {
        Map<String, Double> inventoryRequestMap = new HashMap<>();
        inventoryRequestMap.put("A001", 1.0);

        ResponseEntity<List<InventoryResponse>> responseEntity = restTemplate.exchange(
            uri("/api/inventory"),
            POST,
            new HttpEntity<>(inventoryRequestMap),
            new ParameterizedTypeReference<>() {
            }
        );

        assertThat(responseEntity.getStatusCode())
            .isEqualTo(HttpStatus.OK);
        List<InventoryResponse> inventoryResponseList = List.of(new InventoryResponse("A001", 6, true));
        assertThat(responseEntity.getBody()).isEqualTo(inventoryResponseList);

        JdbcTestUtils.deleteFromTables(jdbcClient, "inventory");
    }

    private URI uri(String path) {
        return restTemplate.getRestTemplate()
            .getUriTemplateHandler()
            .expand(path);
    }
}
