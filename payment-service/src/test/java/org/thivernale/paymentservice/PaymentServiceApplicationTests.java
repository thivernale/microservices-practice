package org.thivernale.paymentservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;

@SpringBootTest
@EmbeddedKafka(
    partitions = 1,
    brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"},
//    kraft = true,
    adminTimeout = 1
)
class PaymentServiceApplicationTests {

    @Test
    void contextLoads() {
    }

}
