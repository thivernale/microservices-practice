package org.thivernale.productservice.service;

import org.springframework.stereotype.Service;
import org.thivernale.productservice.dto.ProductRequest;
import org.thivernale.productservice.dto.ProductResponse;
import org.thivernale.productservice.model.Category;
import org.thivernale.productservice.model.Product;

@Service
public class ProductMapper {
    public ProductResponse fromProduct(Product product) {
        return ProductResponse.builder()
            .id(product.getId())
            .name(product.getName())
            .description(product.getDescription())
            .price(product.getPrice())
            .categoryId(product.getCategory() == null ? null : product.getCategory()
                .getId())
            .build();
    }

    public Product toProduct(ProductRequest productRequest) {
        return Product.builder()
            .name(productRequest.getName())
            .description(productRequest.getDescription())
            .price(productRequest.getPrice())
            .category(Category.builder()
                .id(productRequest.getCategoryId())
                .build())
            .build();
    }
}
