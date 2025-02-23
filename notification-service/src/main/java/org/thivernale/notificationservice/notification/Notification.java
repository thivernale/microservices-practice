package org.thivernale.notificationservice.notification;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.thivernale.notificationservice.notification.event.OrderPlacedEvent;
import org.thivernale.notificationservice.notification.event.PaymentEvent;

import java.time.LocalDateTime;

@Document(collection = "notification")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Notification {
    @Id
    private String id;
    private NotificationType notificationType;
    private LocalDateTime notificationTime;
    private OrderPlacedEvent orderPlacedEvent;
    private PaymentEvent paymentEvent;
}
