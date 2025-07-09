package org.thivernale.paymentservice.wallet.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thivernale.paymentservice.wallet.dto.CancelPaymentTransactionRequest;
import org.thivernale.paymentservice.wallet.model.PaymentTransaction;
import org.thivernale.paymentservice.wallet.model.PaymentTransactionStatus;
import org.thivernale.paymentservice.wallet.model.Refund;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RefundMapperTest {
    private RefundMapper refundMapper;
    @Mock
    private PaymentTransactionService paymentTransactionService;

    @BeforeEach
    void setUp() {
        refundMapper = new RefundMapper(paymentTransactionService);
    }

    @Test
    public void shouldMapToRefund() {
        CancelPaymentTransactionRequest request = TestDataUtil.createRefundRequest();
        PaymentTransaction paymentTransaction = PaymentTransaction.builder()
            .id(100L)
            .amount(BigDecimal.valueOf(100.0))
            .currency("BGN")
            .build();

        when(paymentTransactionService.findById(request.paymentTransactionId()))
            .thenReturn(Optional.of(paymentTransaction));

        Refund actual = refundMapper.toRefund(request);

        Refund expected = Refund.builder()
            .paymentTransaction(paymentTransaction)
            .amount(request.amount())
            .currency(request.currency())
            .status(PaymentTransactionStatus.SUCCESS)
            .reason(request.reason())
            .build();

        assertThat(actual)
            .usingRecursiveComparison()
            .isEqualTo(expected);
    }
}
