package org.thivernale.customerservice.controller;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class WireMockConfig {

    @Bean(initMethod = "start", destroyMethod = "stop")
    public WireMockServer wireMockServer() {
        return new WireMockServer(WireMockConfiguration.wireMockConfig()
            .port(1032)
            .withRootDirectory("src/test/resources/wiremock")
//            .extensions(new Jetty12GrpcExtensionFactory())
            .extensionScanningEnabled(true)
        );
    }
}
