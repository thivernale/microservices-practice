package org.thivernale.paymentservice.exchangerates.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.thivernale.paymentservice.exchangerates.model.ExchangeRate;
import org.thivernale.paymentservice.wallet.model.CurrencyType;

import java.util.Optional;

public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {
    Optional<ExchangeRate> findByCurrency(CurrencyType currency);
}
