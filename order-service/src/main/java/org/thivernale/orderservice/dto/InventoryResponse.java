package org.thivernale.orderservice.dto;

public record InventoryResponse(String skuCode, double quantity, boolean inStock) {
}
