package org.thivernale.customerservice;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.test.context.DynamicPropertyRegistrar;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.kafka.ConfluentKafkaContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;
import org.wiremock.integrations.testcontainers.WireMockContainer;

import java.io.IOException;
import java.util.List;

@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {
    static WireMockContainer wiremockServer;

    static {
        try {
            wiremockServer = new WireMockContainer("wiremock/wiremock:3.13.0")
                .withCliArg("--verbose")
                .withCliArg("--global-response-templating")
                .withCopyFileToContainer(
                    MountableFile.forClasspathResource("wiremock/grpc/protos.dsc"),
                    "/home/wiremock/grpc/protos.dsc")
                .withExtensions(
                    List.of(),
                    new DefaultResourceLoader(TestcontainersConfiguration.class.getClassLoader())
                        .getResource("wiremock/extensions")
                        .getFile()
                );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Bean
    DynamicPropertyRegistrar apiPropertiesRegistrar(
        WireMockContainer wiremockServer,
        MongoDBContainer mongoDBContainer,
        ConfluentKafkaContainer kafkaContainer
    ) {
        return registry -> {
            registry.add("billing.service.address", wiremockServer::getHost);
            registry.add("billing.service.port", wiremockServer::getPort);
            registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
            registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
        };
    }

    @Bean
    WireMockContainer wireMockContainer() {
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

    @Bean
    public NewTopic paymentTopic(@Value("${spring.kafka.template.default-topic:}") String customerTopic) {
        return TopicBuilder.name(customerTopic)
            .build();
    }
}
