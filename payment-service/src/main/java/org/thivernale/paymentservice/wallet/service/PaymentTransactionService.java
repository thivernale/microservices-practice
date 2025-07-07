package org.thivernale.paymentservice.wallet.service;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thivernale.paymentservice.wallet.dto.CreatePaymentTransactionRequest;
import org.thivernale.paymentservice.wallet.model.PaymentTransaction;
import org.thivernale.paymentservice.wallet.repository.PaymentTransactionRepository;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentTransactionService {
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final PaymentTransactionMapper paymentTransactionMapper;

    public Optional<PaymentTransaction> findById(Long id) {
        return paymentTransactionRepository.findById(id);
    }

    public Optional<PaymentTransaction> findByIdWithRefunds(Long id) {
        return paymentTransactionRepository.findByIdWithRefunds(id);
    }

    public PaymentTransaction save(CreatePaymentTransactionRequest request) {
        return paymentTransactionRepository.save(paymentTransactionMapper.toPaymentTransaction(request));
    }

    public BigDecimal calculateOutstandingAmount(
        PaymentTransaction payment,
        @NotNull(message = "Currency should be specified")
        String targetCurrency
    ) {
        return convert(payment.getAmount(), payment.getCurrency(), targetCurrency)
            .subtract(payment.getRefunds()
                .stream()
                .map(refund -> convert(refund.getAmount(), refund.getCurrency(), targetCurrency))
                .reduce(BigDecimal.ZERO, BigDecimal::add));
    }

    private BigDecimal convert(BigDecimal amount, String sourceCurrency, String targetCurrency) {
        // TODO currency conversion into target currency
        return amount;
    }
}
