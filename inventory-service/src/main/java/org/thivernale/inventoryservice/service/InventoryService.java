package org.thivernale.inventoryservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thivernale.inventoryservice.dto.InventoryResponse;
import org.thivernale.inventoryservice.model.Inventory;
import org.thivernale.inventoryservice.repository.InventoryRepository;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @Transactional
    public List<InventoryResponse> getInventory(final Map<String, Double> inventoryRequestMap, boolean reserve) {

        List<Inventory> inventoryList = inventoryRepository.findBySkuCodeIn(inventoryRequestMap.keySet());

        List<InventoryResponse> inventoryResponseList = inventoryList
            .stream()
            .map(inventory -> new InventoryResponse(
                    inventory.getSkuCode(),
                    inventory.getQuantity(),
                    inventory.getQuantity() >= inventoryRequestMap.get(inventory.getSkuCode())
                )
            )
            .toList();

        if (reserve && inventoryResponseList.size() == inventoryRequestMap.size()) {
            inventoryList.forEach(inventory -> inventory.setQuantity(inventory.getQuantity() - inventoryRequestMap.get(inventory.getSkuCode())));
            inventoryRepository.saveAll(inventoryList);
        }

        return inventoryResponseList;
    }
}
