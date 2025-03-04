package org.thivernale.orderservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.thivernale.orderservice.dto.InventoryResponse;

import java.util.List;
import java.util.Map;

@FeignClient(name = "INVENTORY-SERVICE", path = "/api/inventory")
public interface InventoryClient {
    @PostMapping
    List<InventoryResponse> isInStock(
        @RequestBody Map<String, Double> inventoryRequestMap,
        @RequestParam(name = "reserve", defaultValue = "false", required = false) boolean reserve
    );
}
