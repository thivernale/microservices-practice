package org.thivernale.paymentservice.wallet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.thivernale.paymentservice.wallet.model.CurrencyAccount;

public interface CurrencyAccountRepository extends JpaRepository<CurrencyAccount, Long> {
}
