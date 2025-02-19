package org.thivernale.orderservice.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.thivernale.orderservice.dto.CustomerResponse;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.thivernale.orderservice.client.OrderMocks.setupMockCustomerResponse;

@SpringBootTest(classes = {CustomerClient.class})
@ActiveProfiles("test-customer")
class CustomerClientIntegrationTest extends BaseClientIntegrationTest {

    @Autowired
    private CustomerClient customerClient;

    @BeforeEach
    void setUp() throws IOException {
        setupMockCustomerResponse(mockServer);
        setupMockCustomerResponse(mockServer2);
    }

    @Test
    public void whenGetCustomer_thenCustomerShouldBeReturned() {
        assertThat(customerClient.findById("customer-123")).isNotNull();
    }

    @Test
    public void whenGetCustomer_thenCorrectCustomerShouldBeReturned() {
        customerClient.findById("customer-123")
            .map(customerResponse -> assertThat(customerResponse)
                .isEqualTo(new CustomerResponse("customer-123", "Customer", "123", "customer123@example.com")))
            .orElseThrow();
    }
}
