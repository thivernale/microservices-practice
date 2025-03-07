package org.thivernale.orderservice.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.bind.annotation.*;
import org.thivernale.orderservice.dto.InventoryResponse;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@ActiveProfiles("eureka-test")
@Configuration
@RestController
@RequestMapping("/api/inventory")
public class MockInventoryServiceConfig {
    @PostMapping
    List<InventoryResponse> getInventory(
        @RequestBody Map<String, Double> inventoryRequestMap,
        @RequestParam(name = "reserve", defaultValue = "false", required = false) boolean reserve
    ) throws IOException {
        InputStream inputStream = getClass()
            .getClassLoader()
            .getResourceAsStream("payload/get-inventory-availability-response.json");
        ObjectMapper objectMapper = new ObjectMapper();

        // deserialize generic type with Jackson
        return objectMapper.readValue(inputStream, new TypeReference<>() {
        });
    }
}
