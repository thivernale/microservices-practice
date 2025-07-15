package org.thivernale.customerservice.grpc;

import billing.BillingResponse;
import billing.BillingServiceGrpc;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.wiremock.grpc.dsl.WireMockGrpcService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.wiremock.grpc.dsl.WireMockGrpc.json;
import static org.wiremock.grpc.dsl.WireMockGrpc.method;

/**
 * Powered by https://github.com/wiremock/wiremock-grpc-extension/blob/main/wiremock-grpc-extension-jetty12/src/test
 * /java/org/wiremock/grpc/Jetty12GrpcViaExtensionScanningTest.java
 */
class BillingServiceGrpcClientTest {
    @RegisterExtension
    public static WireMockExtension wm = WireMockExtension.newInstance()
        .options(WireMockConfiguration.wireMockConfig()
            .dynamicPort()
            .withRootDirectory("src/test/resources/wiremock")
            .extensionScanningEnabled(true)
        )
        .build();

    WireMock wireMock;
    WireMockGrpcService wireMockGrpcService;
    ManagedChannel channel;
    BillingServiceGrpcClient client;

    @BeforeEach
    void setUp() {
        wireMock = wm.getRuntimeInfo()
            .getWireMock();
        wireMockGrpcService = new WireMockGrpcService(
            wireMock,
            "BillingService"
        );
        channel = ManagedChannelBuilder.forAddress("localhost", wm.getPort())
            .usePlaintext()
            .build();
        client = new BillingServiceGrpcClient(BillingServiceGrpc.newBlockingStub(channel));
    }

    @AfterEach
    void tearDown() {
        channel.shutdown();
    }

    @Test
    protected void mockBillingResponse() {
        BillingResponse billingResponse = BillingResponse.newBuilder()
            .setAccountId("account-id")
            .setStatus("success")
            .build();

        wireMockGrpcService.stubFor(
            method("CreateBillingAccount")
                // .willReturn(message(billingResponse))
                .willReturn(json("{ \"accountId\": \"account-id\", \"status\": \"success\" }"))
        );

        BillingResponse billingAccount = client.createBillingAccount("c", "n", "n@x.com");

        assertThat(billingAccount)
            .usingRecursiveAssertion()
            .isEqualTo(billingResponse);
    }
}
