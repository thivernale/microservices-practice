package org.thivernale.notificationservice.notification.event;

import org.thivernale.notificationservice.notification.PaymentMethod;
import org.thivernale.notificationservice.notification.event.domain.Customer;
import org.thivernale.notificationservice.notification.event.domain.Product;

import java.math.BigDecimal;
import java.util.List;

public record OrderPlacedEvent(
    String orderReference,
    BigDecimal totalAmount,
    PaymentMethod paymentMethod,
    Customer customer,
    List<Product> products
) {
}
