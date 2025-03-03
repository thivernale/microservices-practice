package org.thivernale.inventoryservice.dto;

public record InventoryResponse(String skuCode, double quantity, boolean inStock) {
}
