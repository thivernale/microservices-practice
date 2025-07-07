package org.thivernale.paymentservice.wallet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.thivernale.paymentservice.wallet.model.Refund;

public interface RefundRepository extends JpaRepository<Refund, Long> {
}
