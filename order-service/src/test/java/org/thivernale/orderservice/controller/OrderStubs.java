package org.thivernale.orderservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.matching.AnythingPattern;
import com.github.tomakehurst.wiremock.matching.EqualToPattern;
import com.github.tomakehurst.wiremock.matching.MultiValuePattern;
import org.thivernale.orderservice.dto.CustomerResponse;
import org.thivernale.orderservice.dto.InventoryResponse;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

public class OrderStubs {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void setupMockGetInventoryAvailability(
        WireMockServer mockServer,
        String skuCode,
        List<InventoryResponse> inventoryResponseList
    ) {
        mockServer.stubFor(get(urlPathEqualTo("/api/inventory"))
            //.withQueryParam("sku-code", new EqualToPattern(skuCode))
            .withQueryParam("sku-code", MultiValuePattern.of(new AnythingPattern()))
            .willReturn(aResponse()
                .withStatus(OK.value())
                .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .withBody(writeValueAsString(inventoryResponseList))
            )
        );
    }

    public static void setupMockGetCustomer(
        WireMockServer mockServer,
        String customerId,
        CustomerResponse customerResponse
    ) {
        mockServer.stubFor(get(urlPathTemplate("/api/customer/{id}"))
            .withPathParam("id", new EqualToPattern(customerId))
            .willReturn(aResponse()
                .withStatus(OK.value())
                .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .withBody(writeValueAsString(customerResponse))
            )
        );
    }

    public static void setupMockGetCustomerNotFound(
        WireMockServer mockServer,
        String id
    ) {
        mockServer.stubFor(get(urlPathTemplate("/api/customer/{id}"))
            .withPathParam("id", new EqualToPattern(id))
            .willReturn(aResponse()
                .withStatus(NOT_FOUND.value())
                .withHeader(CONTENT_TYPE, TEXT_PLAIN_VALUE)
                .withBody("Cannot find customer with id %s".formatted(id))
            )
        );
    }

    public static void setupMockCreatePayment(WireMockServer mockServer) {
        mockServer.stubFor(post(urlPathEqualTo("/api/payment"))
            .willReturn(aResponse()
                .withStatus(OK.value())
                .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .withBody(Long.valueOf(100L)
                    .toString())
            )
        );
    }

    private static String writeValueAsString(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
