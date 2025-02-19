package org.thivernale.orderservice.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.thivernale.orderservice.dto.InventoryResponse;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.thivernale.orderservice.client.OrderMocks.setupMockInventoryAvailabilityResponse;

@SpringBootTest(classes = {InventoryClient.class})
@ActiveProfiles("test")
public class InventoryClientIntegrationTest extends BaseClientIntegrationTest {

    @Autowired
    private InventoryClient inventoryClient;

    @BeforeEach
    void setUp() throws IOException {
        setupMockInventoryAvailabilityResponse(this.mockServer);
        setupMockInventoryAvailabilityResponse(mockServer2);
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
