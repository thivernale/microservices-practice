package org.thivernale.orderservice.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.thivernale.orderservice.model.Order;

import java.util.List;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(OrderNowRepository.class)
class OrderNowRepositoryTest {
    private final OrderNowRepository repository;
    private final JdbcClient jdbcClient;

    @Autowired
    OrderNowRepositoryTest(OrderNowRepository repository, JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
        this.repository = repository;
    }

    @Test
    public void shouldFindNoResults() {
        List<Order> orderList = repository.findAll();
        Assertions.assertNotNull(orderList);
        Assertions.assertEquals(JdbcTestUtils.countRowsInTable(this.jdbcClient, "orders"), orderList.size());
    }
}
