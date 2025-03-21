package org.thivernale.inventoryservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.thivernale.inventoryservice.model.Inventory;

import java.util.Collection;
import java.util.List;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    List<Inventory> findBySkuCodeIn(Collection<String> skuCode);
}
