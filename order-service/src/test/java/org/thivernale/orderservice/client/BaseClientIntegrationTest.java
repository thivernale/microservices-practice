package org.thivernale.orderservice.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;

@EnableFeignClients
@EnableAutoConfiguration
@Import({WireMockConfig.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BaseClientIntegrationTest {
    @Autowired
    protected WireMockServer mockServer;
    @Autowired
    protected WireMockServer mockServer2;

    @AfterAll
    void tearDown() {
        mockServer.shutdownServer();
        mockServer2.shutdownServer();
    }
}
