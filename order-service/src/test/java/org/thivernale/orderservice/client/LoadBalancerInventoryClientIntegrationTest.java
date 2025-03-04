package org.thivernale.orderservice.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.cloud.client.loadbalancer.reactive.ReactiveLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ReactorLoadBalancer;
import org.springframework.cloud.loadbalancer.core.RoundRobinLoadBalancer;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.cloud.loadbalancer.support.ServiceInstanceListSuppliers;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.test.context.ActiveProfiles;
import org.thivernale.orderservice.dto.InventoryResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.thivernale.orderservice.client.ClientTestDataUtil.getInventoryRequestMap;
import static org.thivernale.orderservice.client.OrderMocks.setupMockInventoryAvailabilityResponse;

@SpringBootTest(classes = {TestConfig.class, InventoryClient.class})
@ActiveProfiles("test")
@EnableFeignClients
@EnableAutoConfiguration
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LoadBalancerInventoryClientIntegrationTest {
    @Autowired
    private WireMockServer mockServer;
    @Autowired
    private WireMockServer mockServer2;
    @Autowired
    private InventoryClient inventoryClient;
    @Autowired
    private LoadBalancerClientFactory clientFactory;

    private static DefaultServiceInstance instance(String serviceId, String host, int port, boolean secure) {
        return new DefaultServiceInstance(serviceId, serviceId, host, port, secure);
    }

    @BeforeEach
    void setUp() throws IOException {
        setupMockInventoryAvailabilityResponse(mockServer);
        setupMockInventoryAvailabilityResponse(mockServer2);

        String serviceId = "INVENTORY-SERVICE";
        RoundRobinLoadBalancer loadBalancer =
            new RoundRobinLoadBalancer(ServiceInstanceListSuppliers.toProvider(serviceId,
                instance(serviceId, "localhost", 1030, false),
                instance(serviceId, "localhost", 1031, false)
            ), serviceId, -1);
    }

    @AfterAll
    void tearDown() {
        mockServer.shutdownServer();
        mockServer2.shutdownServer();
    }

    @Test
    public void whenCheckAvailability_thenRequestsAreLoadBalanced() {
        for (int i = 0; i < 10; i++) {
            inventoryClient.isInStock(getInventoryRequestMap(), false);
        }

        mockServer.verify(moreThan(0), postRequestedFor(urlPathEqualTo("/api/inventory")));
        mockServer2.verify(moreThan(0), postRequestedFor(urlPathEqualTo("/api/inventory")));
    }

    @Test
    public void whenCheckAvailability_thenTheCorrectAvailabilityShouldBeReturned() {
        assertTrue(inventoryClient.isInStock(getInventoryRequestMap(), false)
            .containsAll(List.of(
                new InventoryResponse("001", 200, true),
                new InventoryResponse("002", 0, false)
            )));
    }

    @Test
    public void loadbalancerWorks() throws IOException {
        setupMockInventoryAvailabilityResponse(mockServer);
        setupMockInventoryAvailabilityResponse(mockServer2);

        String serviceId = "INVENTORY-SERVICE";
        ReactiveLoadBalancer<ServiceInstance> reactiveLoadBalancer = this.clientFactory.getInstance(serviceId,
            ReactiveLoadBalancer.class, ServiceInstance.class);

        then(reactiveLoadBalancer)
            .isInstanceOf(RoundRobinLoadBalancer.class);
        then(reactiveLoadBalancer)
            .isInstanceOf(ReactorLoadBalancer.class);

        ReactorLoadBalancer<ServiceInstance> loadBalancer =
            (ReactorLoadBalancer<ServiceInstance>) reactiveLoadBalancer;

        for (int i = 0; i < 10; i++) {
            inventoryClient.isInStock(getInventoryRequestMap(), false);
        }

        // order dependent on seedPosition -1 of RoundRobinLoadBalancer
        List<String> hosts = Arrays.asList("localhost", "localhost");

        assertLoadBalancer(loadBalancer, hosts);

        mockServer.verify(moreThan(0), postRequestedFor(urlPathEqualTo("/api/inventory")));
        mockServer2.verify(moreThan(0), postRequestedFor(urlPathEqualTo("/api/inventory")));
    }

    private void assertLoadBalancer(ReactorLoadBalancer<ServiceInstance> loadBalancer, List<String> hosts) {
        for (String host : hosts) {
            Mono<Response<ServiceInstance>> source = loadBalancer.choose();
            StepVerifier.create(source)
                .consumeNextWith(response -> {
                    then(response)
                        .isNotNull();
                    then(response.hasServer())
                        .isTrue();

                    ServiceInstance instance = response.getServer();
                    then(instance)
                        .isNotNull();
                    then(instance.getHost()).as("instance host is incorrect %s", host)
                        .isEqualTo(host);
                    if (host.contains("secure")) {
                        then(instance.isSecure()).isTrue();
                    } else {
                        then(instance.isSecure()).isFalse();
                    }
                })
                .verifyComplete();
        }
    }

}
