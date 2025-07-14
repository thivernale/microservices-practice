package org.thivernale.customerservice.controller;

import billing.BillingResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.kafka.ConfluentKafkaContainer;
import org.thivernale.customerservice.TestcontainersConfiguration;
import org.thivernale.customerservice.dto.CustomerRequest;
import org.thivernale.customerservice.model.Address;
import org.wiremock.grpc.dsl.WireMockGrpcService;
import org.wiremock.integrations.testcontainers.WireMockContainer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.anything;
import static org.springframework.http.MediaType.TEXT_PLAIN;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.wiremock.grpc.dsl.WireMockGrpc.message;
import static org.wiremock.grpc.dsl.WireMockGrpc.method;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureDataMongo
@Import({TestcontainersConfiguration.class, WireMockConfig.class})
class CustomerControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ConfluentKafkaContainer kafkaContainer;
    @Autowired
    private WireMockContainer wireMockContainer;

    @BeforeEach
    void setUp() {
        mockBillingResponse(wireMockContainer.getPort());
    }

    protected void mockBillingResponse(int port) {
        WireMockGrpcService wireMockGrpcService = new WireMockGrpcService(
            new WireMock(port),
            "BillingService"
        );
        BillingResponse billingResponse = BillingResponse.newBuilder()
            .setAccountId("account-id")
            .setStatus("success")
            .build();

        wireMockGrpcService.stubFor(
            method("CreateBillingAccount")
                //.withRequestMessage(new AnythingPattern())
                .willReturn(message(billingResponse))
//                .willReturn(json("{ \"accountId\": \"account-id\", \"status\": \"success\" }"))
        );
    }

    @Test
    public void shouldBeRunning() {
        assertThat(kafkaContainer.isCreated())
            .isTrue();
        assertThat(kafkaContainer.isRunning())
            .isTrue();
    }

    @Test
    public void shouldCreateCustomer() throws Exception {
        mockMvc.perform(post("/api/customer")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(getCustomerRequest()))
            )
            .andExpect(status().isCreated())
            .andExpect(content().contentTypeCompatibleWith(TEXT_PLAIN))
            .andExpect(content().string(anything()));
    }

    @Test
    public void shouldGetEmptyListOfCustomers() throws Exception {
        mockMvc.perform(get("/api/customer"))
            .andExpect(status()
                .isOk())
            .andExpect(jsonPath("$")
                .isEmpty())
            .andExpect(content().json("[]"));
    }

    private CustomerRequest getCustomerRequest() {
        return new CustomerRequest(
            null,
            "First",
            "Last",
            "first.last@example.com",
            new Address("Street", "House number", "Zip code")
        );
    }
}
