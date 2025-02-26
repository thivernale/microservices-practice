package org.thivernale.productservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.thivernale.productservice.dto.ProductRequest;
import org.thivernale.productservice.model.Category;
import org.thivernale.productservice.model.Product;
import org.thivernale.productservice.repository.CategoryRepository;
import org.thivernale.productservice.repository.ProductRepository;
import org.thivernale.productservice.service.ProductMapper;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.TEXT_PLAIN;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureMockMvc
@AutoConfigureDataMongo
@Transactional
class ProductControllerTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:latest"))
            /*.withCopyFileToContainer(
                MountableFile.forClasspathResource("init-schema.js"),
                "/docker-entrypoint-initdb.d/init-script.js")*/;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Test
    public void shouldBeRunning() {
        assertThat(mongoDBContainer.isCreated()).isTrue();
        assertThat(mongoDBContainer.isRunning()).isTrue();
    }

    @Test
    public void shouldCreateProduct() throws Exception {
        mockMvc.perform(post("/api/product")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(getProductRequest())))
            .andExpect(status().isCreated())
            .andExpect(content().contentTypeCompatibleWith(TEXT_PLAIN))
            .andExpect(content().string(anything()));

        assertThat(productRepository.findAll()
            .size()).isEqualTo(1);
    }

    @Test
    public void shouldGetEmptyListOfProducts() throws Exception {
        mockMvc.perform(get("/api/product"))
            .andExpect(status().isOk())
            .andExpect(content().json("[]"));
    }

    @Test
    public void shouldGetListOfProducts() throws Exception {
        Product product = productMapper.toProduct(getProductRequest());
        Category category = categoryRepository.save(product.getCategory());
        product.setCategory(category);
        productRepository.save(product);

        mockMvc.perform(get("/api/product"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()", equalTo(1)))
            .andExpect(jsonPath("$[0].id", anything()))
            .andExpect(jsonPath("$[0].name", equalTo(product.getName())))
            .andExpect(jsonPath("$[0].description", equalTo(product.getDescription())))
            .andExpect(jsonPath("$[0].categoryId", equalTo(product.getCategory()
                .getId()
                .intValue())))
            .andExpect(jsonPath("$[0].price", equalTo(product.getPrice()
                .intValue())))
        //.andDo(print())
        ;
    }

    private ProductRequest getProductRequest() {
        return ProductRequest.builder()
            .name("Test Name")
            .description("Test Description")
            .price(BigDecimal.valueOf(1_200))
            .categoryId(BigInteger.valueOf(123))
            .build();
    }
}
