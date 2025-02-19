package org.thivernale.orderservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.thivernale.orderservice.dto.CustomerResponse;

import java.util.Optional;

@FeignClient(name = "CUSTOMER-SERVICE", path = "/api/customer/")
public interface CustomerClient {
    @GetMapping("{id}")
    Optional<CustomerResponse> findById(@PathVariable String id);
}
