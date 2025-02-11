package org.thivernale.productservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thivernale.productservice.dto.CategoryResponse;
import org.thivernale.productservice.repository.CategoryRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final ObjectMapper objectMapper;

    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll()
            .stream()
            .map(category -> objectMapper.convertValue(category, CategoryResponse.class))
            .toList();
    }
}
