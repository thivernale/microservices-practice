package org.thivernale.paymentservice.wallet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.thivernale.paymentservice.wallet.model.PaymentTransaction;

import java.util.Optional;

public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {
    @Query("SELECT pt FROM PaymentTransaction pt LEFT JOIN FETCH pt.refunds r WHERE pt.id = ?1")
    Optional<PaymentTransaction> findByIdWithRefunds(Long id);
}
