package org.thivernale.paymentservice.wallet.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.thivernale.paymentservice.util.BaseEntity;

import java.math.BigDecimal;

@Entity
@Table(name = "refunds")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
@EntityListeners(AuditingEntityListener.class)
public class Refund extends BaseEntity {
    @Column(nullable = false)
    private BigDecimal amount;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentTransactionStatus status;
    @ManyToOne
    @JoinColumn(nullable = false)
    private PaymentTransaction paymentTransaction;
    private String reason;
}
