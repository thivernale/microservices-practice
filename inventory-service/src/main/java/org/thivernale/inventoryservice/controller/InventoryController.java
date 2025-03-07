package org.thivernale.inventoryservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.thivernale.inventoryservice.dto.InventoryResponse;
import org.thivernale.inventoryservice.service.InventoryService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public List<InventoryResponse> getInventory(
        @RequestBody Map<String, Double> inventoryRequestMap,
        @RequestParam(name = "reserve", defaultValue = "false", required = false) boolean reserve
    ) {
        return inventoryService.getInventory(inventoryRequestMap, reserve);
    }
}
