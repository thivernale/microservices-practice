package org.thivernale.orderservice.client;

import org.thivernale.orderservice.dto.InventoryResponse;

import java.util.List;
import java.util.Map;

public final class ClientTestDataUtil {
    private ClientTestDataUtil() {
    }

    public static Map<String, Double> getInventoryRequestMap() {
        return Map.of("001", 1.0, "002", 1.0);
    }

    public static List<InventoryResponse> getInventoryResponseList() {
        return List.of(
            new InventoryResponse("001", 200, true),
            new InventoryResponse("002", 0, false)
        );
    }
}
