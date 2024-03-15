package org.thivernale.productservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.thivernale.productservice.dto.ProductRequest;
import org.thivernale.productservice.model.Product;
import org.thivernale.productservice.repository.ProductRepository;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class ProductControllerTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:latest"));

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Test
    public void shouldBeRunning() {
        assertTrue(mongoDBContainer.isCreated());
        assertTrue(mongoDBContainer.isRunning());
    }

    @Test
    public void shouldCreateProduct() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(getProductRequest())))
            .andExpect(MockMvcResultMatchers.status()
                .isCreated());

        assertEquals(productRepository.findAll()
            .size(), 1);
    }

    @Test
    public void shouldGetEmptyListOfProducts() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/product"))
            .andExpect(MockMvcResultMatchers.status()
                .isOk())
            .andExpect(MockMvcResultMatchers.content()
                .json("[]"));
    }

    @Test
    public void shouldGetListOfProducts() throws Exception {

        Product product = objectMapper.convertValue(getProductRequest(), Product.class);
        productRepository.save(product);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/product"))
            .andExpect(MockMvcResultMatchers.status()
                .isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.length()", Matchers.equalTo(1)))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].id", Matchers.anything()))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].name", Matchers.equalTo(product.getName())))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].description", Matchers.equalTo(product.getDescription())))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].price", Matchers.equalTo(product.getPrice()
                .intValue())))
            .andDo(MockMvcResultHandlers.print())
        ;
    }

    private ProductRequest getProductRequest() {
        return ProductRequest.builder()
            .name("Test Name")
            .description("Test Description")
            .price(BigDecimal.valueOf(1_200))
            .build();
    }
}
