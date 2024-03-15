package org.thivernale.orderservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.thivernale.orderservice.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
