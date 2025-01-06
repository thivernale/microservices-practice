package org.thivernale.orderservice.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.test.context.ActiveProfiles;
import org.thivernale.orderservice.dto.InventoryResponse;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.thivernale.orderservice.client.OrderMocks.setupMockInventoryAvailabilityResponse;

@SpringBootTest(classes = {WireMockConfig.class, InventoryClient.class})
@ActiveProfiles("test")
@EnableFeignClients
@EnableAutoConfiguration
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class InventoryClientIntegrationTest {
    @Autowired
    private WireMockServer mockServer;
    @Autowired
    private WireMockServer mockServer2;
    @Autowired
    private InventoryClient inventoryClient;

    @BeforeEach
    void setUp() throws IOException {
        setupMockInventoryAvailabilityResponse(mockServer);
        setupMockInventoryAvailabilityResponse(mockServer2);
    }

    @AfterAll
    void tearDown() {
        mockServer.shutdownServer();
        mockServer2.shutdownServer();
    }

    @Test
    public void whenCheckAvailability_thenAvailabilityShouldBeReturned() {
        assertFalse(inventoryClient.isInStock(List.of("001", "002"))
            .isEmpty());
    }

    @Test
    public void whenCheckAvailability_thenTheCorrectAvailabilityShouldBeReturned() {
        assertTrue(inventoryClient.isInStock(List.of("001", "002"))
            .containsAll(List.of(
                new InventoryResponse("001", true, 200),
                new InventoryResponse("002", false, 0)
            )));
    }
}
