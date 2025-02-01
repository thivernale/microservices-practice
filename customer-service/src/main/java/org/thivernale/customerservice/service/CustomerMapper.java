package org.thivernale.customerservice.service;

import org.springframework.stereotype.Service;
import org.thivernale.customerservice.dto.CustomerRequest;
import org.thivernale.customerservice.dto.CustomerResponse;
import org.thivernale.customerservice.model.Customer;

@Service
public class CustomerMapper {
    CustomerResponse fromCustomer(Customer customer) {
        return new CustomerResponse(
            customer.getId(),
            customer.getFirstName(),
            customer.getLastName(),
            customer.getEmail(),
            customer.getAddress());
    }

    Customer toCustomer(CustomerRequest customerRequest) {
        if (customerRequest == null) {
            return null;
        }
        return Customer.builder()
            .id(customerRequest.id())
            .firstName(customerRequest.firstName())
            .lastName(customerRequest.lastName())
            .email(customerRequest.email())
            .email(customerRequest.email())
            .address(customerRequest.address())
            .build();
    }
}
