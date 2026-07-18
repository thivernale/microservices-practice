package org.thivernale.paymentservice.wallet.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thivernale.paymentservice.exchangerates.model.ExchangeRate;
import org.thivernale.paymentservice.exchangerates.repository.ExchangeRateRepository;
import org.thivernale.paymentservice.wallet.model.CurrencyType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CurrencyConverter {
    public static final CurrencyType BASE_CURRENCY = CurrencyType.USD;
    public static final int AMOUNT_SCALE = 2;
    public static final int RATE_SCALE = 12;

    private final ExchangeRateRepository exchangeRateRepository;

    public Optional<BigDecimal> getRate(CurrencyType from, CurrencyType to) {
        if (Objects.equals(from, to)) {
            return Optional.of(BigDecimal.ONE);
        }

        // Keeps 12 decimal places regardless of how large the exchange rate is
        return resolveRates(from, to)
            .map(rates -> rates.to()
                .divide(rates.from(), RATE_SCALE, RoundingMode.HALF_UP));
    }

    public BigDecimal convert(CurrencyType from, CurrencyType to, BigDecimal amount) {
        if (Objects.equals(from, to)) {
            return amount.setScale(AMOUNT_SCALE, RoundingMode.HALF_UP);
        }

        CurrencyRates rates = resolveRates(from, to)
            .orElseThrow(() -> new EntityNotFoundException(
                "Exchange rate not found for %s/%s".formatted(from, to)));

        // Multiply then divide in one step so the result isn't rounded twice
        // (once for an intermediate rate, once for the final amount).
        return rates.to()
            .multiply(amount)
            .divide(rates.from(), AMOUNT_SCALE, RoundingMode.HALF_UP);
    }

    private Optional<CurrencyRates> resolveRates(CurrencyType from, CurrencyType to) {
        Optional<BigDecimal> rateFrom = BASE_CURRENCY.equals(from) ? Optional.of(BigDecimal.ONE) :
            exchangeRateRepository.findByCurrency(from)
                .map(ExchangeRate::getRate);
        Optional<BigDecimal> rateTo = BASE_CURRENCY.equals(to) ? Optional.of(BigDecimal.ONE) :
            exchangeRateRepository.findByCurrency(to)
                .map(ExchangeRate::getRate);

        if (rateFrom.isPresent() && rateTo.isPresent()) {
            return Optional.of(new CurrencyRates(rateFrom.get(), rateTo.get()));
        }

        return Optional.empty();
    }

    private record CurrencyRates(BigDecimal from, BigDecimal to) {
    }

    /**
     * Subtracts {@code delta} from {@code balance}, keeping {@code balance}'s original scale.
     * Centralizes the balance-rounding policy so account-mutating services don't each
     * re-implement it (a prior per-call-site MathContext misuse silently mis-rounded balances).
     */
    public static BigDecimal subtractFromBalance(BigDecimal balance, BigDecimal delta) {
        return balance.subtract(delta)
            .setScale(balance.scale(), RoundingMode.HALF_UP);
    }
}
