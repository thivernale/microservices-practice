package org.thivernale.orderservice.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.thivernale.orderservice.client.ClientTestDataUtil.getInventoryRequestMap;
import static org.thivernale.orderservice.client.ClientTestDataUtil.getInventoryResponseList;
import static org.thivernale.orderservice.client.OrderMocks.setupMockInventoryAvailabilityResponse;

@SpringBootTest(classes = {InventoryClient.class})
@ActiveProfiles("test")
public class InventoryClientIntegrationTest extends BaseClientIntegrationTest {

    @Autowired
    private InventoryClient inventoryClient;

    @BeforeEach
    void setUp() throws IOException {
        setupMockInventoryAvailabilityResponse(mockServer);
        setupMockInventoryAvailabilityResponse(mockServer2);
    }

    @Test
    public void whenCheckAvailability_thenAvailabilityShouldBeReturned() {
        assertFalse(inventoryClient.getInventory(getInventoryRequestMap(), false)
            .isEmpty());
    }

    @Test
    public void whenCheckAvailability_thenTheCorrectAvailabilityShouldBeReturned() {
        assertTrue(inventoryClient.getInventory(getInventoryRequestMap(), false)
            .containsAll(getInventoryResponseList()));
    }
}
