package org.thivernale.orderservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.thivernale.orderservice.model.PaymentMethod;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class OrderRequest {
    private Long id;
    private String reference;
    @Positive(message = "Order amount should be positive")
    private BigDecimal totalAmount;
    @NotNull(message = "Payment method should be specified")
    private PaymentMethod paymentMethod;
    @NotBlank(message = "Customer should be specified")
    private String customerId;
    @NotEmpty(message = "Order should contain items")
    private List<OrderLineItemDto> items;
}
