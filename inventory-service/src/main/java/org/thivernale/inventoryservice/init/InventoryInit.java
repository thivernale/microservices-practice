package org.thivernale.inventoryservice.init;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thivernale.inventoryservice.model.Inventory;
import org.thivernale.inventoryservice.repository.InventoryRepository;

import java.util.List;

@Configuration
public class InventoryInit {
    @Bean
    @ConditionalOnProperty(
        prefix = "app.datasource",
        value = "init",
        havingValue = "true"
    )
    public CommandLineRunner loadData(InventoryRepository inventoryRepository) {
        return args -> {
            if (0 == inventoryRepository.count()) {
                Iterable<? extends Inventory> inventoryList = List.of(
                    new Inventory(null, "A001", 10),
                    new Inventory(null, "B001", 12)
                );
                inventoryRepository.saveAll(inventoryList);
            }
        };
    }
}
