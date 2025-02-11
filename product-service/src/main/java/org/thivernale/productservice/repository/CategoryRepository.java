package org.thivernale.productservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.thivernale.productservice.model.Category;

public interface CategoryRepository extends MongoRepository<Category, String> {
}
