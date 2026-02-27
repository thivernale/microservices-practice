package org.thivernale.orderservice.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.thivernale.orderservice.model.Order;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @Override
    @EntityGraph(attributePaths = {"items"})
    @NonNull
    List<Order> findAll();

    @EntityGraph(attributePaths = {"items"})
    @Query("SELECT o FROM Order o")
        // Explicitly define the query
    List<Order> findAllWithItems();
}
