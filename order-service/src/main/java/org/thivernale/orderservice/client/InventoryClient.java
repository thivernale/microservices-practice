package org.thivernale.orderservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.thivernale.orderservice.dto.InventoryResponse;

import java.util.List;
import java.util.Map;

@FeignClient(
    name = "inventory-service",
    path = "/api/inventory",
    url = "${app.urls.inventory-service:http://inventory-service}"
)
public interface InventoryClient {
    @PostMapping
    List<InventoryResponse> getInventory(
        @RequestBody Map<String, Double> inventoryRequestMap,
        @RequestParam(name = "reserve", defaultValue = "false", required = false) boolean reserve
    );
}
