package org.thivernale.paymentservice.wallet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.thivernale.paymentservice.wallet.model.BankAccount;

public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {
}
