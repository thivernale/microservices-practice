package org.thivernale.orderservice.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

@TestConfiguration
@ActiveProfiles("test")
public class TestConfig {
    @Bean(initMethod = "start", destroyMethod = "stop")
    public WireMockServer mockServer() {
        return new WireMockServer(options()
            .port(1030));
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public WireMockServer mockServer2() {
        return new WireMockServer(options()
            .port(1031));
    }
}
