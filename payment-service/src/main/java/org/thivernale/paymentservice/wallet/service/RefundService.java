package org.thivernale.paymentservice.wallet.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thivernale.paymentservice.wallet.dto.CancelPaymentTransactionRequest;
import org.thivernale.paymentservice.wallet.exception.InsufficientOutstandingAmountException;
import org.thivernale.paymentservice.wallet.model.CurrencyAccount;
import org.thivernale.paymentservice.wallet.model.PaymentTransaction;
import org.thivernale.paymentservice.wallet.model.Refund;
import org.thivernale.paymentservice.wallet.repository.RefundRepository;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional
public class RefundService {
    private final RefundRepository refundRepository;
    private final RefundMapper refundMapper;
    private final CurrencyAccountService currencyAccountService;
    private final PaymentTransactionService paymentTransactionService;

    public Refund save(CancelPaymentTransactionRequest request) {
        return refundRepository.save(refundMapper.toRefund(request));
    }

    public Refund create(@NotNull @Valid CancelPaymentTransactionRequest request) {
        // TODO currency conversion

        Long paymentTransactionId = request.paymentTransactionId();

        PaymentTransaction paymentTransaction = paymentTransactionService.findByIdWithRefunds(paymentTransactionId)
            .orElseThrow(() -> new EntityNotFoundException("Payment transaction not found, id: " + paymentTransactionId));

        // validate payment transaction and outstanding amount
        BigDecimal outstandingAmount = paymentTransactionService.calculateOutstandingAmount(paymentTransaction, request.currency());
        if (request.amount()
            .compareTo(outstandingAmount) > 0) {
            throw new InsufficientOutstandingAmountException(paymentTransactionId, outstandingAmount, request.amount());
        }

        subtractFromCurrencyAccountBalance(paymentTransaction.getSource(), request.amount()
            .negate());
        if (paymentTransaction.getDestination() != null) {
            subtractFromCurrencyAccountBalance(paymentTransaction.getDestination(), request.amount());
        }

        return save(request);
    }

    private void subtractFromCurrencyAccountBalance(CurrencyAccount account, BigDecimal delta) {
        account.setBalance(account.getBalance()
            .subtract(delta));
        currencyAccountService.save(account);
    }
}
