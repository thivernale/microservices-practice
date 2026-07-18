package org.thivernale.paymentservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
    // Provide a placeholder to prevent Kafka from complaining about null bootstrap servers
    "spring.kafka.bootstrap-servers=localhost:9092",
    // Prevent the listeners from automatically spinning up and looking for active nodes
    "spring.kafka.listener.auto-startup=false"
})
class PaymentServiceApplicationTests {

    @Test
    void contextLoads() {
    }

}
