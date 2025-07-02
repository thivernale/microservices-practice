package org.thivernale.customerservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.thivernale.customerservice.model.Customer;

public interface CustomerRepository extends MongoRepository<Customer, String> {
    boolean existsByEmail(String email);
}
