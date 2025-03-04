package org.thivernale.orderservice.client;

import brave.Span;
import brave.Tracer;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.thivernale.orderservice.dto.InventoryResponse;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class InventoryRestClient {

    private final RestClient.Builder restClientBuilder;
    private final Tracer tracer;

    public List<InventoryResponse> fetchInventory(Map<String, Double> inventoryRequestMap, boolean reserve) {
        Span inventoryServiceLookup = tracer.nextSpan()
            .name("InventoryServiceLookup");

        try (Tracer.SpanInScope ignored = tracer.withSpanInScope(inventoryServiceLookup.start())) {
            return restClientBuilder
                .baseUrl("http://INVENTORY-SERVICE")
                .build()
                .post()
                .uri("/api/inventory", uriBuilder -> uriBuilder.queryParam("reserve", reserve)
                    .build())
                .contentType(MediaType.APPLICATION_JSON)
                .body(inventoryRequestMap)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
        } finally {
            inventoryServiceLookup.finish();
        }
    }
}
