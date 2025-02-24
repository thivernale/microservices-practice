package org.thivernale.orderservice.controller;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.thivernale.orderservice.dto.InventoryResponse;
import org.thivernale.orderservice.dto.OrderLineItemDto;
import org.thivernale.orderservice.dto.OrderRequest;
import org.thivernale.orderservice.dto.OrderResponse;
import org.thivernale.orderservice.exception.BusinessException;
import org.thivernale.orderservice.service.OrderService;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;
    private final DiscoveryClient discoveryClient;

    @PostMapping("/async")
    @ResponseStatus(HttpStatus.CREATED)
    @CircuitBreaker(name = "inventory", fallbackMethod = "fallbackMethod")
    @TimeLimiter(name = "inventory")
    @Retry(name = "inventory")
    public CompletableFuture<?> placeOrder(@RequestBody @Valid OrderRequest orderRequest) {
        return CompletableFuture.supplyAsync(() -> {
                orderService.placeOrder(orderRequest);
                return null;
            })
            .exceptionally(throwable -> {
                return CompletableFuture.failedFuture(new BusinessException(throwable.getMessage()));
            });
    }

    public CompletableFuture<?> fallbackMethod(OrderRequest orderRequest, RuntimeException exception) {
        log.warn(exception.getMessage());
        return CompletableFuture.supplyAsync(() -> null);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Long createOrder(@RequestBody @Valid OrderRequest orderRequest) {
        return orderService.placeOrder(orderRequest);
    }

    @PostMapping("check-availability")
    public List<InventoryResponse> checkAvailability(@RequestBody OrderRequest orderRequest) {
        return orderService.checkAvailability(orderRequest.getItems()
            .stream()
            .map(OrderLineItemDto::getSkuCode)
            .toList());
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<OrderResponse> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("{id}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable("id") Long id) {
        return ResponseEntity.ok(orderService.findById(id));
    }

    @GetMapping("service-instances/{applicationName}")
    public List<ServiceInstance> getServiceInstances(@PathVariable("applicationName") String serviceId) {
        return discoveryClient.getInstances(serviceId);
    }

    @PostMapping("/send-test-event")
    public ResponseEntity<Void> sendTestEvent() {
        orderService.sendTestEvent();
        return ResponseEntity.accepted()
            .build();
    }
}
