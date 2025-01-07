package org.thivernale.orderservice.client;

import org.jetbrains.annotations.NotNull;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.lifecycle.Startables;

import java.util.stream.Stream;

@TestConfiguration
@ActiveProfiles("eureka-test")
public class EurekaContainerConfig {
    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        public static GenericContainer eurekaServer =
            new GenericContainer("springcloud/demo-eureka-server").withExposedPorts(8761);

        @Override
        public void initialize(@NotNull ConfigurableApplicationContext applicationContext) {
            Startables.deepStart(Stream.of(eurekaServer))
                .join();

            TestPropertyValues.of("eureka.client.service-url.defaultZone=http://localhost:" + eurekaServer.getFirstMappedPort()
                    .toString() + "/eureka")
                .applyTo(applicationContext);
        }
    }
}
