package org.thivernale.paymentservice.wallet.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thivernale.paymentservice.wallet.model.CurrencyAccount;
import org.thivernale.paymentservice.wallet.repository.CurrencyAccountRepository;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    public Map<Long, CurrencyAccount> findAll(Set<Long> ids) {
        ids = ids.stream()
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());

        if (ids.isEmpty()) {
            return Collections.emptyMap();
        }

        List<CurrencyAccount> allById = currencyAccountRepository.findAllById(ids);
        Set<Long> foundIds = allById.stream()
            .map(CurrencyAccount::getId)
            .collect(Collectors.toSet());

        ids.removeAll(foundIds);
        if (!ids.isEmpty()) {
            throw new EntityNotFoundException("Currency Account with ids " + ids + " not found");
        }

        return allById.stream()
            .collect(Collectors.toMap(CurrencyAccount::getId, Function.identity()));
    }
}
