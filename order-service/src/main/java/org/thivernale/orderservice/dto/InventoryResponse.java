package org.thivernale.orderservice.dto;

public record InventoryResponse(
    String skuCode,
    boolean inStock,
    Integer quantity
) {
}
