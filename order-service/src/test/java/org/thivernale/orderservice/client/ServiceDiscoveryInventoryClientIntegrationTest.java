package org.thivernale.orderservice.client;

import com.netflix.discovery.EurekaClient;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Lazy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.thivernale.orderservice.OrderServiceApplication;
import org.thivernale.orderservice.dto.InventoryResponse;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(
    classes = {OrderServiceApplication.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ContextConfiguration(
    classes = {MockInventoryServiceConfig.class}/*,
    initializers = {EurekaContainerConfig.Initializer.class}*/
                                                )
@ActiveProfiles("eureka-test")
@EnableConfigurationProperties
public class ServiceDiscoveryInventoryClientIntegrationTest {
    @Autowired
    private InventoryClient inventoryClient;
    @Autowired
    @Lazy
    private EurekaClient eurekaClient;

    @BeforeEach
    void setUp() {
        Awaitility.await()
            .atMost(60, TimeUnit.SECONDS)
            .until(() -> eurekaClient.getApplications()
                .size() > 0);
    }

    /**
     * NB: ensure that Eureka is running for test to pass (start DiscoveryServerApplication)
     */
    @Test
    public void whenCheckAvailability_thenTheCorrectAvailabilityShouldBeReturned() {
        assertTrue(inventoryClient.isInStock(List.of("001", "002"))
            .containsAll(List.of(
                new InventoryResponse("001", true, 200),
                new InventoryResponse("002", false, 0)
            )));
    }
}
