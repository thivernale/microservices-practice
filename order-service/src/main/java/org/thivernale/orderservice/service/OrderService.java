package org.thivernale.orderservice.service;

import brave.Span;
import brave.Tracer;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thivernale.orderservice.client.CustomerClient;
import org.thivernale.orderservice.client.InventoryClient;
import org.thivernale.orderservice.client.InventoryRestClient;
import org.thivernale.orderservice.client.PaymentClient;
import org.thivernale.orderservice.dto.*;
import org.thivernale.orderservice.event.OrderPlacedEvent;
import org.thivernale.orderservice.exception.BusinessException;
import org.thivernale.orderservice.model.Order;
import org.thivernale.orderservice.repository.OrderRepository;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    private final Tracer tracer;

    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    private final InventoryClient inventoryClient;
    private final InventoryRestClient inventoryRestClient;
    private final CustomerClient customerClient;
    private final PaymentClient paymentClient;

    public void placeOrder(OrderRequest orderRequest) {
        var customer = customerClient.findById(orderRequest.getCustomerId())
            .orElseThrow(() -> new BusinessException("Customer not found"));

        // check availability
        Set<String> skuCodes = orderRequest.getItems()
            .stream()
            .map(OrderLineItemDto::getSkuCode)
            .collect(Collectors.toSet());
        List<InventoryResponse> inventoryResponseList = inventoryRestClient.fetchInventory(skuCodes);

        boolean allProductsInStock = skuCodes.size() == inventoryResponseList.size() && inventoryResponseList.stream()
            .allMatch(InventoryResponse::isInStock);

        if (allProductsInStock) {
            Order order = orderMapper.toOrder(orderRequest);
            order.setOrderNumber(UUID.randomUUID()
                .toString());
            order.setItems(orderRequest.getItems()
                .stream()
                .map(orderMapper::toOrderLineItem)
                .peek(orderLineItem -> orderLineItem.setOrder(order))
                .toList());

            orderRepository.save(order);

            paymentClient.createPayment(new PaymentRequest(
                order.getTotalAmount(),
                order.getPaymentMethod(),
                order.getId(),
                orderRequest.getReference(),
                customer
            ));

            kafkaTemplate.send("notificationTopic", new OrderPlacedEvent(order.getOrderNumber()));
        } else {
            throw new BusinessException("Product is not in stock, please try again later");
        }
    }

    public List<InventoryResponse> checkAvailability(List<String> skuCodes) {
        List<InventoryResponse> inStockList;

        Span inventoryServiceClient = tracer.nextSpan()
            .name("InventoryServiceClient");

        try (Tracer.SpanInScope spanInScope = tracer.withSpanInScope(inventoryServiceClient.start())) {
            inStockList = inventoryClient.isInStock(skuCodes);
        } finally {
            inventoryServiceClient.finish();
        }
        return inStockList;
    }

    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll()
            .stream()
            .map(orderMapper::fromOrder)
            .toList();
    }

    public OrderResponse findById(Long id) {
        return orderRepository.findById(id)
            .map(orderMapper::fromOrder)
            .orElseThrow(() -> new EntityNotFoundException(String.format("Order with id %d not found", id)));
    }

    public void sendTestEvent() {
        // in order to test go to terminal of Kafka broker container and run command:
        // > kafka-console-consumer --topic codeTopic --from-beginning --bootstrap-server localhost:9092
        kafkaTemplate.send(
            "codeTopic",
            String.valueOf(Instant.now()
                .toEpochMilli()),
            new OrderPlacedEvent("999-list")
        );
    }
}
