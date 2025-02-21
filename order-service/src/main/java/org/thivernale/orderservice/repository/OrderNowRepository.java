package org.thivernale.orderservice.repository;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.thivernale.orderservice.model.Order;

import java.util.List;

@Repository
public class OrderNowRepository {
    private final JdbcClient jdbcClient;

    public OrderNowRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public List<Order> findAll() {
        return jdbcClient
            .sql("select * from orders")
            .query(Order.class)
            .list();
    }
}
