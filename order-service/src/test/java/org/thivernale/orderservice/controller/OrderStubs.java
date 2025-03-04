package org.thivernale.orderservice.controller;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.common.Json;
import com.github.tomakehurst.wiremock.matching.EqualToPattern;
import org.thivernale.orderservice.dto.CustomerResponse;
import org.thivernale.orderservice.dto.InventoryResponse;

import java.util.List;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

public class OrderStubs {

    public static void setupMockGetInventoryAvailability(
        WireMockServer mockServer,
        Map<String, Double> inventoryRequestMap,
        List<InventoryResponse> inventoryResponseList
    ) {
        mockServer.stubFor(post(urlPathEqualTo("/api/inventory"))
            //.withQueryParam("reserve", new AnythingPattern())
            .withRequestBody(equalToJson(Json.write(inventoryRequestMap)))
            .willReturn(jsonResponse(inventoryResponseList, OK.value()))
        );
    }

    public static void setupMockGetCustomer(
        WireMockServer mockServer,
        String customerId,
        CustomerResponse customerResponse
    ) {
        mockServer.stubFor(get(urlPathTemplate("/api/customer/{id}"))
            .withPathParam("id", new EqualToPattern(customerId))
            .willReturn(jsonResponse(customerResponse, OK.value()))
        );
    }

    public static void setupMockGetCustomerNotFound(
        WireMockServer mockServer,
        String id
    ) {
        mockServer.stubFor(get(urlPathTemplate("/api/customer/{id}"))
            .withPathParam("id", new EqualToPattern(id))
            .willReturn(notFound()
                .withHeader(CONTENT_TYPE, TEXT_PLAIN_VALUE)
                .withBody("Cannot find customer with id %s".formatted(id))
            )
        );
    }

    public static void setupMockCreatePayment(WireMockServer mockServer) {
        mockServer.stubFor(post(urlPathEqualTo("/api/payment"))
            .willReturn(jsonResponse(100L, OK.value()))
        );
    }
}
