package org.thivernale.productservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.thivernale.productservice.model.Category;
import org.thivernale.productservice.repository.CategoryRepository;
import org.thivernale.productservice.repository.ProductRepository;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

//@Component
@RequiredArgsConstructor
public class Init implements CommandLineRunner {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Override
    public void run(String... args) throws Exception {
        List<Category> categories = categoryRepository.findAll();
        int size = categories.size();
        ThreadLocalRandom random = ThreadLocalRandom.current();
        productRepository.findAll()
            .stream()
            .filter(product -> product.getCategory() == null)
            .forEach(product -> {
                product.setCategory(categories.get(random.nextInt(size)));
                productRepository.save(product);
            });
    }
}
