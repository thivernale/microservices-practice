package org.thivernale.orderservice.event;

import org.thivernale.orderservice.dto.CustomerResponse;
import org.thivernale.orderservice.dto.OrderLineItemDto;
import org.thivernale.orderservice.model.PaymentMethod;

import java.math.BigDecimal;
import java.util.List;

public record OrderPlacedEvent(
    String orderReference,
    BigDecimal totalAmount,
    PaymentMethod paymentMethod,
    CustomerResponse customer,
    List<OrderLineItemDto> products
) {
}
