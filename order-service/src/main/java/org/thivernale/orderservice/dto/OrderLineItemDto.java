package org.thivernale.orderservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Validated
public class OrderLineItemDto {
    private Long id;
    @NotBlank
    private String skuCode;
    @NotBlank
    private String productId;
    @Positive
    @NotNull
    private BigDecimal price;
    @Positive
    @NotNull
    private double quantity;
}
