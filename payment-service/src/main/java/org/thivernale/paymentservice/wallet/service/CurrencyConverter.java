package org.thivernale.paymentservice.wallet.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thivernale.paymentservice.exchangerates.model.ExchangeRate;
import org.thivernale.paymentservice.exchangerates.repository.ExchangeRateRepository;
import org.thivernale.paymentservice.wallet.model.CurrencyType;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CurrencyConverter {
    public static final CurrencyType BASE_CURRENCY = CurrencyType.USD;

    private final ExchangeRateRepository exchangeRateRepository;

    public Optional<BigDecimal> getRate(CurrencyType from, CurrencyType to) {
        if (Objects.equals(from, to)) {
            return Optional.of(BigDecimal.ONE);
        }

        Optional<BigDecimal> rateFrom = BASE_CURRENCY.equals(from) ? Optional.of(BigDecimal.ONE) :
            exchangeRateRepository.findByCurrency(from)
                .map(ExchangeRate::getRate);
        Optional<BigDecimal> rateTo = BASE_CURRENCY.equals(to) ? Optional.of(BigDecimal.ONE) :
            exchangeRateRepository.findByCurrency(to)
                .map(ExchangeRate::getRate);

        if (rateFrom.isPresent() && rateTo.isPresent()) {
            return Optional.of(rateTo.get()
                .divide(rateFrom.get(), new MathContext(6, RoundingMode.HALF_UP)));
        }

        return Optional.empty();
    }

    public BigDecimal convert(CurrencyType from, CurrencyType to, BigDecimal amount) {
        return getRate(from, to).orElseThrow(EntityNotFoundException::new)
            .multiply(amount);
    }
}
