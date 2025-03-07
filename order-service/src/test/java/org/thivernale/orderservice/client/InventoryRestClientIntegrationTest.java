package org.thivernale.orderservice.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.thivernale.orderservice.config.WebClientConfig;
import org.thivernale.orderservice.dto.InventoryResponse;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.thivernale.orderservice.client.ClientTestDataUtil.getInventoryRequestMap;
import static org.thivernale.orderservice.client.ClientTestDataUtil.getInventoryResponseList;
import static org.thivernale.orderservice.client.OrderMocks.setupMockInventoryAvailabilityResponse;

@SpringBootTest
@ContextConfiguration(classes = {InventoryRestClient.class, WebClientConfig.class})
@ActiveProfiles("test")
class InventoryRestClientIntegrationTest extends BaseClientIntegrationTest {
    @Autowired
    private InventoryRestClient inventoryRestClient;

    @BeforeEach
    void setUp() throws IOException {
        setupMockInventoryAvailabilityResponse(mockServer);
        setupMockInventoryAvailabilityResponse(mockServer2);
    }

    @Test
    void getInventory() {
        assertThatNoException().isThrownBy(() -> {
            List<InventoryResponse> inventoryResponseList =
                inventoryRestClient.getInventory(getInventoryRequestMap(), true);

            assertThat(inventoryResponseList)
                .isNotEmpty()
                .containsExactlyInAnyOrderElementsOf(getInventoryResponseList());
        });
    }
}
