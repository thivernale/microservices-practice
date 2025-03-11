package org.thivernale.inventoryservice.service;

import org.thivernale.inventoryservice.model.Inventory;

import java.util.List;
import java.util.Map;

public final class TestDataUtil {

    private TestDataUtil() {
    }

    static List<Inventory> getInventoryList() {
        return List.of(
            new Inventory(1L, "item1", 10.0),
            new Inventory(2L, "item2", 10.0),
            new Inventory(3L, "item3", 10.0)
        );
    }

    static Map<String, Double> getInventoryRequestMap() {
        return Map.of("item1", 1.0, "item2", 2.0, "item3", 3.0);
    }

    static List<String> getSkuCodeList() {
        return List.of("item1", "item2", "item3");
    }
}
