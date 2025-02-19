package org.thivernale.orderservice.service;

import brave.Span;
import brave.Tracer;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.thivernale.orderservice.client.CustomerClient;
import org.thivernale.orderservice.client.InventoryClient;
import org.thivernale.orderservice.dto.InventoryResponse;
import org.thivernale.orderservice.dto.OrderLineItemDto;
import org.thivernale.orderservice.dto.OrderRequest;
import org.thivernale.orderservice.dto.OrderResponse;
import org.thivernale.orderservice.event.OrderPlacedEvent;
import org.thivernale.orderservice.exception.BusinessException;
import org.thivernale.orderservice.model.Order;
import org.thivernale.orderservice.model.OrderLineItem;
import org.thivernale.orderservice.repository.OrderRepository;

import java.time.Instant;
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

    private final Tracer tracer;

    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    private final InventoryClient inventoryClient;
    private final CustomerClient customerClient;
    private final OrderMapper orderMapper;

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

        Span inventoryServiceLookup = tracer.nextSpan()
            .name("InventoryServiceLookup");

        try (Tracer.SpanInScope spanInScope = tracer.withSpanInScope(inventoryServiceLookup.start())) {

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

                kafkaTemplate.send("notificationTopic", new OrderPlacedEvent(order.getOrderNumber()));
            } else {
                throw new IllegalArgumentException("Product is not in stock, please try again later");
            }
        } finally {
            inventoryServiceLookup.finish();
        }
    }

    public List<InventoryResponse> checkAvailability(List<String> skuCode) {
        List<InventoryResponse> inStockList;

        Span inventoryServiceClient = tracer.nextSpan()
            .name("InventoryServiceClient");

        try (Tracer.SpanInScope spanInScope = tracer.withSpanInScope(inventoryServiceClient.start())) {
            inStockList = inventoryClient.isInStock(skuCode);
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
