package org.thivernale.orderservice.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients;
import org.springframework.cloud.loadbalancer.core.ReactorLoadBalancer;
import org.springframework.cloud.loadbalancer.core.RoundRobinLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.cloud.loadbalancer.support.ServiceInstanceListSuppliers;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.BDDAssertions.then;

@SpringBootTest
@ContextConfiguration(classes = {TestConfig.class})
@ActiveProfiles("test")
@EnableFeignClients
@EnableAutoConfiguration
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LoadBalancerIntegrationTest {
    @Autowired
    private WireMockServer mockServer;
    @Autowired
    private WireMockServer mockServer2;

    private static DefaultServiceInstance instance(String serviceId, String host, int port, boolean secure) {
        return new DefaultServiceInstance(serviceId, serviceId, host, port, secure);
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

    @AfterAll
    void tearDown() {
        mockServer.shutdownServer();
        mockServer2.shutdownServer();
    }

    @Test
    void staticConfigurationWorks() {
        String serviceId = "test-inventory-service";

        RoundRobinLoadBalancer loadBalancer =
            new RoundRobinLoadBalancer(ServiceInstanceListSuppliers.toProvider(serviceId,
                instance(serviceId, "inventory-service-1", 1030, false),
                instance(serviceId, "inventory-service-2", 1031, false)
            ), serviceId, -1);

        assertLoadBalancer(loadBalancer, Arrays.asList("inventory-service-1", "inventory-service-2"));
    }

    @SpringBootConfiguration(proxyBeanMethods = false)
    @EnableAutoConfiguration
    @EnableCaching
    @LoadBalancerClients(
        value = {@LoadBalancerClient(
            name = "INVENTORY-SERVICE",
            configuration = InventoryServiceConfig.class
        )}
    )
    protected static class Config {
    }

    protected static class InventoryServiceConfig {
        @Bean
        public RoundRobinLoadBalancer roundRobinLoadBalancer(LoadBalancerClientFactory clientFactory, Environment env) {
            String serviceId = LoadBalancerClientFactory.getName(env);
            return new RoundRobinLoadBalancer(clientFactory.getLazyProvider(serviceId,
                ServiceInstanceListSupplier.class), serviceId, -1);
        }
    }
}
