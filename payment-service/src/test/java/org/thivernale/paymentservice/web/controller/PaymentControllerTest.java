package org.thivernale.paymentservice.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.thivernale.paymentservice.payment.PaymentMethod;
import org.thivernale.paymentservice.payment.PaymentService;
import org.thivernale.paymentservice.payment.dto.Customer;
import org.thivernale.paymentservice.payment.dto.PaymentRequest;
import org.thivernale.paymentservice.util.JsonConverter;
import org.thivernale.paymentservice.wallet.notification.PaymentTransactionProducer;
import org.thivernale.paymentservice.web.exception.GlobalExceptionHandler;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // overkill for this test
@WebMvcTest(value = {PaymentController.class, GlobalExceptionHandler.class})
@AutoConfigureMockMvc
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // Use Mockito's Mockito.mock() or MockitoBean to inject into Spring Context
    @MockitoBean
    private PaymentService paymentService;  // Spring will now inject this mock into the controller

    @MockitoBean
    private PaymentTransactionProducer paymentTransactionProducer;

    @MockitoBean
    private JsonConverter jsonConverter;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // prefer @AutoConfigureMockMvc for being closer to real application
        /*MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(new PaymentController(paymentService, null, null))
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();*/
    }

    @Test
    void testCreatePayment_Success() throws Exception {
        PaymentRequest validRequest = new PaymentRequest(
            null,
            BigDecimal.valueOf(200),
            PaymentMethod.VISA,
            100L,
            "ORDER-100",
            new Customer("jane-doe", "Jane", "Doe", "jdoe@example.com")
        );

        when(paymentService.createPayment(validRequest)).thenReturn(1L);

        mockMvc.perform(post("/api/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
            .andExpect(status().isOk())
            .andExpect(content().string("1"));
    }

    @Test
    void testCreatePayment_InvalidRequest() throws Exception {
        PaymentRequest invalidRequest = new PaymentRequest(
            null,
            BigDecimal.valueOf(-50), // Negative amount
            null, // Missing payment method
            null,
            null,
            null
        );

        mockMvc.perform(post("/api/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$.errors")
                .exists());
    }
}
