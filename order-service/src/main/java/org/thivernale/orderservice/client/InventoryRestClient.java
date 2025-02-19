package org.thivernale.orderservice.client;

import brave.Span;
import brave.Tracer;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.thivernale.orderservice.dto.InventoryResponse;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class InventoryRestClient {

    private final RestClient.Builder restClientBuilder;
    private final Tracer tracer;

    public List<InventoryResponse> fetchInventory(Set<String> skuCodes) {
        Span inventoryServiceLookup = tracer.nextSpan()
            .name("InventoryServiceLookup");

        try (Tracer.SpanInScope spanInScope = tracer.withSpanInScope(inventoryServiceLookup.start())) {
            return restClientBuilder.build()
                .get()
                .uri("http://inventory-service/api/inventory", uriBuilder -> uriBuilder.queryParam("sku-code",
                        skuCodes)
                    .build())
                .retrieve()
                .body(new ParameterizedTypeReference<List<InventoryResponse>>() {
                });
        } finally {
            inventoryServiceLookup.finish();
        }
    }
}
