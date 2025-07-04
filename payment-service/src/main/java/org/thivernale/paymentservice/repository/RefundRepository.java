package org.thivernale.paymentservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.thivernale.paymentservice.model.Refund;

public interface RefundRepository extends JpaRepository<Refund, Long> {
}
