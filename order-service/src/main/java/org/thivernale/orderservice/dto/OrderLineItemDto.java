package org.thivernale.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class OrderLineItemDto {
    private Long id;
    private String skuCode;
    private String productId;
    private BigDecimal price;
    private double quantity;
}
