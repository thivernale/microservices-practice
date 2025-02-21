package org.thivernale.orderservice.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.thivernale.orderservice.model.Order;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Component
public class JsonDataLoader implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(JsonDataLoader.class);
    private final ObjectMapper objectMapper;

    public JsonDataLoader(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void run(String... args) {
        try (InputStream inputStream = TypeReference.class.getClassLoader()
            .getResourceAsStream("data/orders.json")) {
            Orders orders = objectMapper.readValue(inputStream, Orders.class);
            log.info("Reading {} records from JSON data", orders.orders()
                .size());
        } catch (IOException e) {
            throw new RuntimeException("Failed to load JSON data", e);
        }
    }

    private record Orders(List<Order> orders) {
    }
}
