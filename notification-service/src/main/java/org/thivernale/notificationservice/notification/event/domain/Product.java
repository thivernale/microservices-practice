package org.thivernale.notificationservice.notification.event.domain;

import java.math.BigDecimal;

public record Product(
    String id,
    String name,
    String description,
    BigDecimal price,
    double quantity
) {
}
