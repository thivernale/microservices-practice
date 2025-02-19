package org.thivernale.orderservice.service;

import org.springframework.stereotype.Service;
import org.thivernale.orderservice.dto.OrderLineItemDto;
import org.thivernale.orderservice.dto.OrderRequest;
import org.thivernale.orderservice.dto.OrderResponse;
import org.thivernale.orderservice.model.Order;

@Service
public class OrderMapper {
    public Order toOrder(OrderRequest orderRequest) {
        return Order.builder()
            .id(orderRequest.getId())
            .customerId(orderRequest.getCustomerId())
            .reference(orderRequest.getReference())
            .paymentMethod(orderRequest.getPaymentMethod())
            .totalAmount(orderRequest.getTotalAmount())
            .build();
    }

    public OrderResponse fromOrder(Order order) {
        return OrderResponse.builder()
            .id(order.getId())
            .orderNumber(order.getOrderNumber())
            .reference(order.getReference())
            .totalAmount(order.getTotalAmount())
            .paymentMethod(order.getPaymentMethod())
            .customerId(order.getCustomerId())
            .items(order.getItems()
                .stream()
                .map(orderLineItem -> OrderLineItemDto.builder()
                    .id(orderLineItem.getId())
                    .skuCode(orderLineItem.getSkuCode())
                    .productId(orderLineItem.getProductId())
                    .price(orderLineItem.getPrice())
                    .quantity(orderLineItem.getQuantity())
                    .build())
                .toList())
            .build();
    }
}
