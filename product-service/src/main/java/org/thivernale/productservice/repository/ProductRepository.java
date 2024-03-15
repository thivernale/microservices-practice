package org.thivernale.productservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.thivernale.productservice.model.Product;

public interface ProductRepository extends MongoRepository<Product, String> {
}
