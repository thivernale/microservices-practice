package org.thivernale.paymentservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.thivernale.paymentservice.model.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
