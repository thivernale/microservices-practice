package org.thivernale.paymentservice.wallet.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thivernale.paymentservice.wallet.dto.CancelPaymentTransactionRequest;
import org.thivernale.paymentservice.wallet.exception.InsufficientOutstandingAmountException;
import org.thivernale.paymentservice.wallet.model.CurrencyAccount;
import org.thivernale.paymentservice.wallet.model.PaymentTransaction;
import org.thivernale.paymentservice.wallet.model.Refund;
import org.thivernale.paymentservice.wallet.repository.RefundRepository;

import java.math.BigDecimal;
import java.math.MathContext;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class RefundService {
    private final RefundRepository refundRepository;
    private final RefundMapper refundMapper;
    private final CurrencyAccountService currencyAccountService;
    private final PaymentTransactionService paymentTransactionService;
    private final CurrencyConverter currencyConverter;

    public Refund save(CancelPaymentTransactionRequest request) {
        return refundRepository.save(refundMapper.toRefund(request));
    }

    public Refund create(@NotNull @Valid CancelPaymentTransactionRequest request) {
        Long paymentTransactionId = request.paymentTransactionId();

        PaymentTransaction paymentTransaction = paymentTransactionService.findByIdWithRefunds(paymentTransactionId)
            .orElseThrow(() -> new EntityNotFoundException("Payment transaction not found, id: " + paymentTransactionId));

        // validate payment transaction and outstanding amount
        BigDecimal outstandingAmount = paymentTransactionService.calculateOutstandingAmount(paymentTransaction);
        if (request.amount()
            .compareTo(outstandingAmount) > 0) {
            throw new InsufficientOutstandingAmountException(paymentTransactionId, outstandingAmount, request.amount());
        }

        CurrencyAccount sourceAccount = paymentTransaction.getSource();
        CurrencyAccount destAccount = paymentTransaction.getDestination();

        subtractFromCurrencyAccountBalance(sourceAccount, request.amount()
            .negate());
        if (destAccount != null) {
            BigDecimal destAmount = sourceAccount.getCurrency()
                .equals(destAccount.getCurrency()) ?
                request.amount() :
                currencyConverter.convert(sourceAccount.getCurrency(), destAccount.getCurrency(), request.amount());
            subtractFromCurrencyAccountBalance(destAccount, destAmount);
        }

        return save(request);
    }

    private void subtractFromCurrencyAccountBalance(CurrencyAccount account, BigDecimal delta) {
        account.setBalance(account.getBalance()
            .subtract(delta, new MathContext(account.getBalance()
                .scale())));
        currencyAccountService.save(account);
    }
}
