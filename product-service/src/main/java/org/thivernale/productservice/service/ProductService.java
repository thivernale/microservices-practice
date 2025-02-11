package org.thivernale.productservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thivernale.productservice.dto.ProductRequest;
import org.thivernale.productservice.dto.ProductResponse;
import org.thivernale.productservice.repository.ProductRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public void createProduct(ProductRequest productRequest) {
        productRepository.save(productMapper.toProduct(productRequest));
    }

    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
            .stream()
            .map(productMapper::fromProduct)
            .toList();
    }

    public Optional<ProductResponse> findById(String id) {
        return productRepository.findById(id)
            .map(productMapper::fromProduct);
    }
}
