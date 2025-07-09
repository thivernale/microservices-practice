package org.thivernale.paymentservice.wallet.service;

import jakarta.persistence.EntityNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thivernale.paymentservice.wallet.dto.CancelPaymentTransactionRequest;
import org.thivernale.paymentservice.wallet.exception.InsufficientOutstandingAmountException;
import org.thivernale.paymentservice.wallet.model.PaymentTransaction;
import org.thivernale.paymentservice.wallet.model.Refund;
import org.thivernale.paymentservice.wallet.repository.RefundRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefundServiceTest {
    private RefundService refundService;
    @Mock
    private RefundRepository refundRepository;
    @Mock
    private CurrencyAccountService currencyAccountService;
    @Mock
    private PaymentTransactionService paymentTransactionService;

    @BeforeEach
    void setUp() {
        refundService = new RefundService(
            refundRepository,
            new RefundMapper(paymentTransactionService),
            currencyAccountService,
            paymentTransactionService
        );
    }

    @Test
    public void whenValid_thenRefundShouldBeCreated() {
        CancelPaymentTransactionRequest request = TestDataUtil.createRefundRequest();
        PaymentTransaction paymentTransaction = PaymentTransaction.builder()
            .amount(request.amount())
            .currency("BGN")
            .source(TestDataUtil.getCurrencyAccount(1L))
            .refunds(List.of())
            .build();
        BigDecimal sourceAccBalance = paymentTransaction.getSource()
            .getBalance();

        when(paymentTransactionService.findById(request.paymentTransactionId()))
            .thenReturn(Optional.of(paymentTransaction));
        when(paymentTransactionService.findByIdWithRefunds(request.paymentTransactionId()))
            .thenReturn(Optional.of(paymentTransaction));
        when(paymentTransactionService.calculateOutstandingAmount(paymentTransaction, request.currency()))
            .thenReturn(paymentTransaction.getAmount());

        refundService.create(request);

        verify(refundRepository, times(1)).save(any(Refund.class));
        verifyNoMoreInteractions(refundRepository);

        // account balance updates on success
        assertThat(request.amount())
            .isEqualByComparingTo(sourceAccBalance.subtract(paymentTransaction.getSource()
                    .getBalance())
                .negate());
    }

    @Test
    public void whenPaymentNotFound_thenThrowEntityNotFoundException() {
        CancelPaymentTransactionRequest request = TestDataUtil.createRefundRequest();

        when(paymentTransactionService.findByIdWithRefunds(request.paymentTransactionId()))
            .thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> refundService.create(request))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessage("Payment transaction not found, id: " + request.paymentTransactionId());

        verify(refundRepository, never()).save(any(Refund.class));
        verifyNoMoreInteractions(refundRepository);
    }

    @Test
    public void whenAmountExceedsOutstandingAmount_thenThrowInsufficientOutstandingAmountException() {
        CancelPaymentTransactionRequest request = TestDataUtil.createRefundRequest();
        PaymentTransaction paymentTransaction = PaymentTransaction.builder()
            .amount(request.amount())
            .currency("BGN")
            .refunds(List.of(
                Refund.builder()
                    .amount(request.amount())
                    .build()
            ))
            .build();

        when(paymentTransactionService.findByIdWithRefunds(request.paymentTransactionId()))
            .thenReturn(Optional.of(paymentTransaction));
        when(paymentTransactionService.calculateOutstandingAmount(paymentTransaction, request.currency()))
            .thenReturn(BigDecimal.ZERO);

        assertThatThrownBy(() -> refundService.create(request))
            .isInstanceOf(InsufficientOutstandingAmountException.class)
            .hasMessage("Refund amount %f exceeds outstanding amount %f of payment transaction %d."
                .formatted(request.amount(), BigDecimal.ZERO, request.paymentTransactionId()));

        verify(refundRepository, never()).save(any(Refund.class));
        verifyNoMoreInteractions(refundRepository);
    }
}
