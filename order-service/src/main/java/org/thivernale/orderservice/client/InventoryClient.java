package org.thivernale.orderservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.thivernale.orderservice.dto.InventoryResponse;

import java.util.List;

@FeignClient(name = "INVENTORY-SERVICE", path = "/api/inventory")
public interface InventoryClient {
    @GetMapping
    List<InventoryResponse> isInStock(@RequestParam("sku-code") List<String> skuCode);
}
