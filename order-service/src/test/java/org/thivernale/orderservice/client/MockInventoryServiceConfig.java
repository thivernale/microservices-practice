package org.thivernale.orderservice.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.thivernale.orderservice.dto.InventoryResponse;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@ActiveProfiles("eureka-test")
@Configuration
@RestController
@RequestMapping("/api/inventory")
public class MockInventoryServiceConfig {
    @GetMapping
    List<InventoryResponse> isInStock(@RequestParam("sku-code") List<String> skuCode) throws IOException {
        InputStream inputStream = getClass()
            .getClassLoader()
            .getResourceAsStream("payload/get-inventory-availability-response.json");
        ObjectMapper objectMapper = new ObjectMapper();

        return objectMapper.readValue(inputStream, new TypeReference<>() {
        });
    }
}
