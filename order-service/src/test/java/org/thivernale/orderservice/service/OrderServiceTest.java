package org.thivernale.orderservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thivernale.orderservice.event.OrderPlacedEvent;
import org.thivernale.orderservice.notification.NotificationProducer;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Captor
    ArgumentCaptor<OrderPlacedEvent> orderPlacedEventArgumentCaptor;
    @Captor
    ArgumentCaptor<String> keyArgumentCaptor;
    private OrderService orderService;
    @Mock
    private NotificationProducer notificationProducer;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(null, null, null, null, null, null, null, notificationProducer);
    }

    @Test
    void sendTestEvent() {
        Instant now = Instant.now();
        orderService.sendTestEvent();

        verify(notificationProducer).sendNotification(orderPlacedEventArgumentCaptor.capture(),
            keyArgumentCaptor.capture(), eq("codeTopic"));

        String key = keyArgumentCaptor.getValue();
        assertThat(Instant.ofEpochMilli(Long.parseLong(key)))
            //.isCloseTo(now, new TemporalUnitLessThanOffset(10, ChronoUnit.MILLIS))
            .isAfter(now);

        OrderPlacedEvent value = orderPlacedEventArgumentCaptor.getValue();
        assertThat(value.orderReference()).isEqualTo("999-list");
    }
}
