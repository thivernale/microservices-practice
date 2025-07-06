package org.thivernale.paymentservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.thivernale.paymentservice.model.Payment;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    @Query("SELECT p FROM Payment p LEFT JOIN FETCH p.refunds")
    Optional<Payment> findByIdWithRefunds(Long id);
}
