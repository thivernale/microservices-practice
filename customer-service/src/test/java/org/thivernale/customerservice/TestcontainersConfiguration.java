package org.thivernale.customerservice;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistrar;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.kafka.ConfluentKafkaContainer;
import org.testcontainers.utility.DockerImageName;
import org.wiremock.integrations.testcontainers.WireMockContainer;

@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {
    static WireMockContainer wiremockServer =
        new WireMockContainer("wiremock/wiremock:3.6.0");

    @Bean
    DynamicPropertyRegistrar apiPropertiesRegistrar(
        WireMockContainer wiremockServer,
        MongoDBContainer mongoDBContainer,
        ConfluentKafkaContainer kafkaContainer
    ) {
        //billing.service.address=localhost
        //billing.service.port=9099
        return registry -> {
            registry.add("billing.service.address", wiremockServer::getHost);
            registry.add("billing.service.port", wiremockServer::getPort);
            registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
            registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
        };
    }

    @Bean
    WireMockContainer wireMockServer() {
        wiremockServer.start();
        WireMock.configureFor(wiremockServer.getHost(), wiremockServer.getPort());
        return wiremockServer;
    }

    @Bean
    @ServiceConnection
    MongoDBContainer mongodbContainer() {
        return new MongoDBContainer(DockerImageName.parse("mongo:latest"));
    }

    @Bean
    @ServiceConnection
    ConfluentKafkaContainer kafkaContainer() {
        return new ConfluentKafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.5.0"));
    }
}
