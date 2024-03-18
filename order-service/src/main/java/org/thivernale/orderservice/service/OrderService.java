package org.thivernale.orderservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.thivernale.orderservice.dto.InventoryResponse;
import org.thivernale.orderservice.dto.OrderLineItemDto;
import org.thivernale.orderservice.dto.OrderRequest;
import org.thivernale.orderservice.dto.OrderResponse;
import org.thivernale.orderservice.model.Order;
import org.thivernale.orderservice.model.OrderLineItem;
import org.thivernale.orderservice.repository.OrderRepository;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderService {
    private final OrderRepository orderRepository;

    private final RestClient.Builder restClientBuilder;

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

        // check availability
        InventoryResponse[] inventoryResponseArray = restClientBuilder.build()
            .get()
            .uri("http://inventory-service/api/inventory", (uriBuilder) -> uriBuilder.queryParam("sku-code",
                    order.getItems()
                        .stream()
                        .map(OrderLineItem::getSkuCode)
                        .toList())
                .build())
            .retrieve()
            .body(InventoryResponse[].class);
        List<InventoryResponse> inventoryResponseList = Arrays.stream(inventoryResponseArray)
            .toList();

        boolean allProductsInStock = inventoryResponseList.stream()
            .allMatch(InventoryResponse::isInStock);

        log.info("{}", inventoryResponseList);

        if (allProductsInStock) {
            orderRepository.save(order);
        } else {
            throw new IllegalArgumentException("Product is not in stock, please try again later");
        }
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
