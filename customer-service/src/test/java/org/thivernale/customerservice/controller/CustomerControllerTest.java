package org.thivernale.customerservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.kafka.ConfluentKafkaContainer;
import org.thivernale.customerservice.TestcontainersConfiguration;
import org.thivernale.customerservice.dto.CustomerRequest;
import org.thivernale.customerservice.model.Address;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.anything;
import static org.springframework.http.MediaType.TEXT_PLAIN;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureDataMongo
@Import(TestcontainersConfiguration.class)
class CustomerControllerTest {
    @LocalServerPort
    private int port;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ConfluentKafkaContainer kafkaContainer;

    // what we need:
    // wiremock for grpc // alt wiremock w/o testcontainers
/*
    @Autowired
    private WireMockContainer wireMockServer;

    @BeforeEach
    void setUp() {
        mockBillingResponse();
    }

    protected void mockBillingResponse() {
        WireMockGrpcService wireMockGrpcService = new WireMockGrpcService(
            new WireMock(wireMockServer.getPort()),
            "BillingService"
        );
        wireMockGrpcService.stubFor(
            WireMockGrpc.method("CreateBillingAccount")
                //.withRequestMessage(new AnythingPattern())
                .willReturn(
                    WireMockGrpc.message(
                        BillingResponse.newBuilder()
                            .setAccountId("account-id")
                            .setStatus("success")
                            .build()
                    )
                    */
/*new GrpcResponseDefinitionBuilder(WireMockGrpc.Status.OK)
                        .fromJson("{ \"accountId\": \"account-id\", \"status\": \"success\" }")*//*

                )
        );
    }
*/

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
