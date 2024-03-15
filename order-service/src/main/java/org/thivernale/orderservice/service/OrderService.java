package org.thivernale.orderservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thivernale.orderservice.dto.OrderLineItemDto;
import org.thivernale.orderservice.dto.OrderRequest;
import org.thivernale.orderservice.dto.OrderResponse;
import org.thivernale.orderservice.model.Order;
import org.thivernale.orderservice.model.OrderLineItem;
import org.thivernale.orderservice.repository.OrderRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;

    public void placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID()
            .toString());
        order.setItems(orderRequest.getItems()
            .stream()
            .map(orderLineItemDto -> {
                OrderLineItem orderLineItem = new OrderLineItem();
                orderLineItem.setSkuCode(orderLineItemDto.getSkuCode());
                orderLineItem.setPrice(orderLineItemDto.getPrice());
                orderLineItem.setQuantity(orderLineItemDto.getQuantity());
                return orderLineItem;
            })
            .toList());

        orderRepository.save(order);
    }

    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll()
            .stream()
            .map(this::mapToProductResponse)
            .toList();
    }

    private OrderResponse mapToProductResponse(Order order) {
        return OrderResponse.builder()
            .id(order.getId())
            .orderNumber(order.getOrderNumber())
            .items(order.getItems()
                .stream()
                .map(orderLineItem -> OrderLineItemDto.builder()
                    .id(orderLineItem.getId())
                    .skuCode(orderLineItem.getSkuCode())
                    .price(orderLineItem.getPrice())
                    .quantity(orderLineItem.getQuantity())
                    .build())
                .toList())
            .build();
    }
}
