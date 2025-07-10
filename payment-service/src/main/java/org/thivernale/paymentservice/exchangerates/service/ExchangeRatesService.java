package org.thivernale.paymentservice.exchangerates.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thivernale.paymentservice.exchangerates.client.ExchangeRateClientService;
import org.thivernale.paymentservice.exchangerates.dto.ExchangeRatesResponse;
import org.thivernale.paymentservice.exchangerates.model.ExchangeRate;
import org.thivernale.paymentservice.exchangerates.repository.ExchangeRateRepository;
import org.thivernale.paymentservice.wallet.model.CurrencyType;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.thivernale.paymentservice.wallet.service.CurrencyConverter.BASE_CURRENCY;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExchangeRatesService {
    private final ExchangeRateRepository repository;
    private final ExchangeRateClientService clientService;

    public ExchangeRatesResponse fetchRates() {
        String symbols = Arrays.stream(CurrencyType.values())
            .map(Enum::name)
            .collect(Collectors.joining(","));

        return clientService.getExchangeRates(BASE_CURRENCY.name(), symbols);
    }

    @Transactional
    public void updateRates(ExchangeRatesResponse rates) {
        log.debug("updateRates: {}", rates);

        if (!Objects.equals(BASE_CURRENCY.name(), rates.base())) {
            throw new IllegalArgumentException("The base currency should be " + BASE_CURRENCY.name());
        }

        rates.rates()
            .forEach((currency, rate) -> {
                CurrencyType currencyType = CurrencyType.fromString(currency);
                ExchangeRate exchangeRate = repository.findByCurrency(currencyType)
                    .orElse(ExchangeRate.builder()
                        .rate(rate)
                        .currency(currencyType)
                        .build());
                repository.save(exchangeRate);
            });
    }

    public boolean hasInitializedExchangeRates() {
        return repository.count() > 0;
    }
}
