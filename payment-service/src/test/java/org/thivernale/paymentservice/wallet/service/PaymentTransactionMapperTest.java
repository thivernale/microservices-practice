package org.thivernale.paymentservice.wallet.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thivernale.paymentservice.wallet.dto.CreatePaymentTransactionRequest;
import org.thivernale.paymentservice.wallet.model.CurrencyAccount;
import org.thivernale.paymentservice.wallet.model.PaymentTransaction;
import org.thivernale.paymentservice.wallet.model.PaymentTransactionStatus;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentTransactionMapperTest {
    private PaymentTransactionMapper paymentTransactionMapper;
    @Mock
    private CurrencyAccountService currencyAccountService;

    @BeforeEach
    void setUp() {
        paymentTransactionMapper = new PaymentTransactionMapper(currencyAccountService);
    }

    @Test
    public void shouldMapToPaymentTransaction() {
        CreatePaymentTransactionRequest request = TestDataUtil.createPaymentRequest();
        Long sourceAccId = request.sourceCurrencyAccountId();
        Long destAccId = request.destCurrencyAccountId();
        CurrencyAccount sourceAcc = TestDataUtil.getCurrencyAccount(sourceAccId);
        CurrencyAccount destAcc = TestDataUtil.getCurrencyAccount(destAccId);

        when(currencyAccountService.findById(sourceAccId))
            .thenReturn(Optional.of(sourceAcc));
        when(currencyAccountService.findById(destAccId))
            .thenReturn(Optional.of(destAcc));

        PaymentTransaction actual = paymentTransactionMapper.toPaymentTransaction(request);

        PaymentTransaction expected = PaymentTransaction.builder()
            .amount(request.amount())
            .currency(request.currency())
            .source(sourceAcc)
            .destination(destAcc)
            .status(PaymentTransactionStatus.SUCCESS)
            .note(request.note())
            .build();

        assertThat(actual)
            .usingRecursiveComparison()
            .isEqualTo(expected);
    }
}
