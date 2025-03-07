package org.thivernale.orderservice.client;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.PostExchange;
import org.thivernale.orderservice.dto.InventoryResponse;

import java.util.List;
import java.util.Map;

public interface InventoryService {

    @PostExchange("/api/inventory")
    List<InventoryResponse> getInventory(
        @RequestBody Map<String, Double> inventoryRequestMap,
        @RequestParam(name = "reserve", defaultValue = "false", required = false) boolean reserve
    );
}
