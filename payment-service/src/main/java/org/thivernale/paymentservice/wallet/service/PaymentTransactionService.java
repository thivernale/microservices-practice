package org.thivernale.paymentservice.wallet.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thivernale.paymentservice.wallet.dto.CreatePaymentTransactionRequest;
import org.thivernale.paymentservice.wallet.exception.InsufficientFundsException;
import org.thivernale.paymentservice.wallet.model.CurrencyAccount;
import org.thivernale.paymentservice.wallet.model.PaymentTransaction;
import org.thivernale.paymentservice.wallet.model.Refund;
import org.thivernale.paymentservice.wallet.repository.PaymentTransactionRepository;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentTransactionService {
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final PaymentTransactionMapper paymentTransactionMapper;
    private final CurrencyAccountService currencyAccountService;

    public Optional<PaymentTransaction> findById(Long id) {
        return paymentTransactionRepository.findById(id);
    }

    public Optional<PaymentTransaction> findByIdWithRefunds(Long id) {
        return paymentTransactionRepository.findByIdWithRefunds(id);
    }

    public PaymentTransaction save(CreatePaymentTransactionRequest request) {
        return paymentTransactionRepository.save(paymentTransactionMapper.toPaymentTransaction(request));
    }

    public BigDecimal calculateOutstandingAmount(PaymentTransaction payment) {
        return payment.getAmount()
            .subtract(payment.getRefunds()
                .stream()
                .map(Refund::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
    }

    public PaymentTransaction create(@NotNull @Valid CreatePaymentTransactionRequest request) {
        Long sourceCurrencyAccountId = request.sourceCurrencyAccountId();
        Long destCurrencyAccountId = request.destCurrencyAccountId();
        // cannot add null in utility methods List.of, Set.of
        Set<Long> ids = new HashSet<>();
        ids.add(sourceCurrencyAccountId);
        ids.add(destCurrencyAccountId);

        Map<Long, CurrencyAccount> accounts = currencyAccountService.findAll(ids);

        CurrencyAccount sourceAccount = accounts.get(sourceCurrencyAccountId);
        CurrencyAccount destAccount = destCurrencyAccountId == null ? sourceAccount : accounts.get(destCurrencyAccountId);

        // validate source balance
        if (sourceAccount
            .getBalance()
            .compareTo(request.amount()) < 0) {
            throw new InsufficientFundsException(sourceCurrencyAccountId, sourceAccount.getBalance(), request.amount());
        }

        subtractFromCurrencyAccountBalance(sourceAccount, request.amount());
        if (!sourceAccount.equals(destAccount)) {
            subtractFromCurrencyAccountBalance(destAccount, request.amount()
                .negate());
        }

        return save(request);
    }

    private void subtractFromCurrencyAccountBalance(CurrencyAccount account, BigDecimal delta) {
        account.setBalance(account.getBalance()
            .subtract(delta));
        currencyAccountService.save(account);
    }
}
