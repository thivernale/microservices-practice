package org.thivernale.paymentservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.thivernale.paymentservice.model.BankAccount;

public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {
}
