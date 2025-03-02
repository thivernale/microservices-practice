package org.thivernale.orderservice;

import org.thivernale.orderservice.dto.*;
import org.thivernale.orderservice.model.PaymentMethod;

import java.math.BigDecimal;
import java.util.List;

public final class TestDataUtil {

    private TestDataUtil() {
    }

    public static OrderRequest createOrderRequest() {
        return OrderRequest.builder()
            .customerId(createCustomer().id())
            .paymentMethod(PaymentMethod.CREDIT_CARD)
            .totalAmount(BigDecimal.valueOf(200))
            .items(List.of(OrderLineItemDto.builder()
                .skuCode("ITEM-123")
                .quantity(1)
                .productId("PRODUCT-ITEM-123")
                .price(BigDecimal.valueOf(200))
                .build()))
            .build();
    }

    public static CustomerResponse createCustomer() {
        return new CustomerResponse(
            "67bc84af1e856a7494958bd3",
            "Jane",
            "Doe",
            "jdoe@example.com"
        );
    }

    public static List<InventoryResponse> createInventoryResponseList() {
        return List.of(new InventoryResponse("ITEM-123", true, 1000));
    }

    public static PaymentRequest createPaymentRequest() {
        return new PaymentRequest(
            100L,
            "OR-100",
            BigDecimal.valueOf(200),
            PaymentMethod.CREDIT_CARD,
            createCustomer()
        );
    }
}
