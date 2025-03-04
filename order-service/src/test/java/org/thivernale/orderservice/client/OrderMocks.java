package org.thivernale.orderservice.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.matching.AnythingPattern;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.Charset;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.springframework.http.HttpStatus.OK;

public class OrderMocks {
    public static void setupMockInventoryAvailabilityResponse(WireMockServer mockServer) throws IOException {
        mockServer.stubFor(post(urlPathEqualTo("/api/inventory"))
            .withRequestBody(new AnythingPattern())
            .willReturn(jsonResponse(
                StreamUtils.copyToString(
                    OrderMocks.class.getClassLoader()
                        .getResourceAsStream("payload/get-inventory-availability-response.json"),
                    Charset.defaultCharset()),
                OK.value()
            ))
        );
    }

    public static void setupMockCustomerResponse(WireMockServer mockServer) throws IOException {
        mockServer.stubFor(get(urlPathTemplate("/api/customer/{id}"))
            .withPathParam("id", new AnythingPattern())
            .willReturn(jsonResponse(
                StreamUtils.copyToString(
                    OrderMocks.class.getClassLoader()
                        .getResourceAsStream("payload/get-customer-response.json"),
                    Charset.defaultCharset()),
                OK.value())
            )
        );
    }
}
