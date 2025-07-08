package org.thivernale.paymentservice.wallet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thivernale.paymentservice.wallet.model.CurrencyAccount;
import org.thivernale.paymentservice.wallet.repository.CurrencyAccountRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CurrencyAccountService {
    private final CurrencyAccountRepository currencyAccountRepository;

    public Optional<CurrencyAccount> findById(Long id) {
        return currencyAccountRepository.findById(id);
    }

    public CurrencyAccount save(CurrencyAccount currencyAccount) {
        return currencyAccountRepository.save(currencyAccount);
    }
}
