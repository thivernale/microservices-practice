package org.thivernale.paymentservice.wallet.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thivernale.paymentservice.wallet.dto.CreatePaymentTransactionRequest;
import org.thivernale.paymentservice.wallet.exception.InsufficientFundsException;
import org.thivernale.paymentservice.wallet.model.CurrencyAccount;
import org.thivernale.paymentservice.wallet.model.PaymentTransaction;
import org.thivernale.paymentservice.wallet.repository.PaymentTransactionRepository;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static org.thivernale.paymentservice.wallet.service.TestDataUtil.createPaymentRequest;
import static org.thivernale.paymentservice.wallet.service.TestDataUtil.getCurrencyAccount;

@ExtendWith(MockitoExtension.class)
class PaymentTransactionServiceTest {
    private PaymentTransactionService paymentTransactionService;
    @Mock
    private PaymentTransactionRepository paymentTransactionRepository;
    @Mock
    private CurrencyAccountService currencyAccountService;
    @Captor
    private ArgumentCaptor<PaymentTransaction> paymentCaptor;

    @BeforeEach
    void setUp() {
        paymentTransactionService = new PaymentTransactionService(
            paymentTransactionRepository,
            new PaymentTransactionMapper(currencyAccountService),
            currencyAccountService
        );
    }

    @Test
    public void whenValid_thenPaymentShouldBeCreated() {
        CreatePaymentTransactionRequest request = createPaymentRequest();
        CurrencyAccount sourceAcc = getCurrencyAccount(request.sourceCurrencyAccountId());
        CurrencyAccount destAcc = getCurrencyAccount(request.destCurrencyAccountId());
        BigDecimal sourceAccBalance = sourceAcc.getBalance();
        BigDecimal destAccBalance = destAcc.getBalance();

        setupAccountMocks(sourceAcc, destAcc, true);

        paymentTransactionService.create(request);

        verify(paymentTransactionRepository, times(1)).save(paymentCaptor.capture());
        verifyNoMoreInteractions(paymentTransactionRepository);

        // account balance updates on success
        assertThat(paymentCaptor.getValue()
            .getAmount())
            .isEqualByComparingTo(sourceAccBalance.subtract(sourceAcc.getBalance()))
            .isEqualByComparingTo(destAccBalance.subtract(destAcc.getBalance())
                .negate());
    }

    @Test
    public void whenAccountNotFound_thenThrowEntityNotFoundException() {
        CreatePaymentTransactionRequest request = createPaymentRequest();
        CurrencyAccount sourceAcc = getCurrencyAccount(request.sourceCurrencyAccountId());
        CurrencyAccount destAcc = getCurrencyAccount(request.destCurrencyAccountId());

        when(currencyAccountService.findAll(Set.of(sourceAcc.getId(), destAcc.getId())))
            .thenThrow(new EntityNotFoundException(
                "Currency Account with ids %s not found".formatted(Set.of(sourceAcc.getId()))));

        assertThatThrownBy(() -> paymentTransactionService.create(request))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining("Currency Account with ids %s not found".formatted(Set.of(sourceAcc.getId())));

        verify(paymentTransactionRepository, never()).save(any(PaymentTransaction.class));
        verifyNoMoreInteractions(paymentTransactionRepository);
    }

    @Test
    public void whenAmountExceedsBalance_thenThrowInsufficientFundsException() {
        CreatePaymentTransactionRequest request = createPaymentRequest();
        CurrencyAccount sourceAcc = getCurrencyAccount(request.sourceCurrencyAccountId());
        CurrencyAccount destAcc = getCurrencyAccount(request.destCurrencyAccountId());

        setupAccountMocks(sourceAcc, destAcc, false);

        sourceAcc.setBalance(request.amount()
            .subtract(BigDecimal.valueOf(0.001)));

        assertThatThrownBy(() -> paymentTransactionService.create(request))
            .isInstanceOf(InsufficientFundsException.class)
            .hasMessage("Insufficient funds in account %d with balance %f to create payment transaction of amount %f."
                .formatted(sourceAcc.getId(), sourceAcc.getBalance(), request.amount()));

        verify(paymentTransactionRepository, never()).save(any(PaymentTransaction.class));
        verifyNoMoreInteractions(paymentTransactionRepository);
    }

    private void setupAccountMocks(CurrencyAccount sourceAcc, CurrencyAccount destAcc, boolean forSuccessfulPath) {
        if (forSuccessfulPath) {
            when(currencyAccountService.findById(sourceAcc.getId()))
                .thenReturn(Optional.of(sourceAcc));
            when(currencyAccountService.findById(destAcc.getId()))
                .thenReturn(Optional.of(destAcc));
        }
        when(currencyAccountService.findAll(Set.of(sourceAcc.getId(), destAcc.getId())))
            .thenReturn(
                Map.of(sourceAcc.getId(), sourceAcc, destAcc.getId(), destAcc)
            );
    }
}
