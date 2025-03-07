package org.thivernale.orderservice.client;

import com.netflix.discovery.EurekaClient;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Lazy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.thivernale.orderservice.client.ClientTestDataUtil.getInventoryRequestMap;
import static org.thivernale.orderservice.client.ClientTestDataUtil.getInventoryResponseList;

@SpringBootTest(
    classes = {MockInventoryServiceConfig.class, InventoryClient.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ContextConfiguration(
    initializers = {EurekaContainerConfig.Initializer.class}
)
@ActiveProfiles("eureka-test")
@EnableConfigurationProperties
@EnableFeignClients
@EnableAutoConfiguration
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
     * NB: ensure that Eureka is running for test to pass (start DiscoveryServerApplication) - see revision
     * NB2: now running with testcontainers Eureka without any other services started
     */
    @Test
    public void whenCheckAvailability_thenTheCorrectAvailabilityShouldBeReturned() {
        assertTrue(inventoryClient.getInventory(getInventoryRequestMap(), false)
            .containsAll(getInventoryResponseList()));
    }
}
