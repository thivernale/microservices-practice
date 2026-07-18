package org.thivernale.paymentservice.wallet.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thivernale.paymentservice.exchangerates.model.ExchangeRate;
import org.thivernale.paymentservice.exchangerates.repository.ExchangeRateRepository;
import org.thivernale.paymentservice.wallet.model.CurrencyType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CurrencyConverterTest {
    static final String USD_TO_EUR_RATE = "0.856150";
    static final int AMOUNT_SCALE = 2;

    private CurrencyConverter currencyConverter;
    @Mock
    private ExchangeRateRepository exchangeRateRepository;

    @BeforeEach
    void setUp() {
        currencyConverter = new CurrencyConverter(exchangeRateRepository);
    }

    @Test
    void whenNoRate_thenReturnEmptyOptional() {
        given(exchangeRateRepository.findByCurrency(CurrencyType.fromString("EUR")))
            .willReturn(Optional.empty());

        Optional<BigDecimal> rate = currencyConverter.getRate(CurrencyType.EUR, CurrencyType.USD);

        assertThat(rate).isEmpty();
    }

    @Test
    void whenSameCurrency_thenReturnOne() {
        Optional<BigDecimal> rate = currencyConverter.getRate(CurrencyType.USD, CurrencyType.USD);

        assertThat(rate).isNotEmpty()
            .get()
            .isEqualTo(BigDecimal.ONE);
    }

    @Test
    void whenValidRate_thenReturnConvertedRate() {
        given(exchangeRateRepository.findByCurrency(CurrencyType.EUR)).willReturn(Optional.of(ExchangeRate.builder()
            .currency(CurrencyType.EUR)
            .rate(new BigDecimal(USD_TO_EUR_RATE))
            .build()));

        BigDecimal expected = BigDecimal.ONE.divide(new BigDecimal(USD_TO_EUR_RATE), 12, RoundingMode.HALF_UP);

        Optional<BigDecimal> rate = currencyConverter.getRate(CurrencyType.EUR, CurrencyType.USD);
        assertThat(rate).isNotEmpty()
            .get()
            .isEqualTo(expected);
    }

    @Test
    void whenInvalidCurrency_thenThrowEntityNotFoundException() {
        given(exchangeRateRepository.findByCurrency(CurrencyType.fromString("EUR")))
            .willReturn(Optional.empty());

        assertThrows(
            EntityNotFoundException.class,
            () -> currencyConverter.convert(CurrencyType.USD, CurrencyType.EUR, new BigDecimal("100.00")));
    }

    @Test
    void whenValidConversion_thenReturnConvertedAmount() {
        given(exchangeRateRepository.findByCurrency(CurrencyType.EUR)).willReturn(Optional.of(ExchangeRate.builder()
            .currency(CurrencyType.EUR)
            .rate(new BigDecimal(USD_TO_EUR_RATE))
            .build()));

        BigDecimal amount = new BigDecimal("100.00");
        BigDecimal expected = amount.multiply(new BigDecimal(USD_TO_EUR_RATE))
            .setScale(AMOUNT_SCALE, RoundingMode.HALF_UP);
        BigDecimal actual = currencyConverter.convert(CurrencyType.USD, CurrencyType.EUR, amount);
        assertThat(actual).isEqualTo(expected);

        expected = amount.divide(new BigDecimal(USD_TO_EUR_RATE), AMOUNT_SCALE, RoundingMode.HALF_UP);
        actual = currencyConverter.convert(CurrencyType.EUR, CurrencyType.USD, amount);
        assertThat(actual).isEqualTo(expected);
    }
}
