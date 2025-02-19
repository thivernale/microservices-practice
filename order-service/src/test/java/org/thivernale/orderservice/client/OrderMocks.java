package org.thivernale.orderservice.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.AnythingPattern;
import com.github.tomakehurst.wiremock.matching.MultiValuePattern;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.Charset;

public class OrderMocks {
    public static void setupMockInventoryAvailabilityResponse(WireMockServer mockServer) throws IOException {
        mockServer.stubFor(WireMock.get(WireMock.urlPathEqualTo("/api/inventory"))
            .withQueryParam("sku-code", MultiValuePattern.of(new AnythingPattern()))
            .willReturn(WireMock.aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(StreamUtils.copyToString(
                    OrderMocks.class.getClassLoader()
                        .getResourceAsStream("payload/get-inventory-availability-response.json"),
                    Charset.defaultCharset()
                ))
            )
        );
    }

    public static void setupMockCustomerResponse(WireMockServer mockServer) throws IOException {
        mockServer.stubFor(WireMock.get(WireMock.urlPathTemplate("/api/customer/{id}"))
            .withPathParam("id", new AnythingPattern())
            .willReturn(WireMock.aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(StreamUtils.copyToString(
                    OrderMocks.class.getClassLoader()
                        .getResourceAsStream("payload/get-customer-response.json"),
                    Charset.defaultCharset()
                ))
            )
        );
    }
}
