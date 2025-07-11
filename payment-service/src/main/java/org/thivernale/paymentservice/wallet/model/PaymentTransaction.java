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
import java.util.List;

@Entity
@Table(name = "payment_transactions")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
@EntityListeners(AuditingEntityListener.class)
public class PaymentTransaction extends BaseEntity {
    @Column(nullable = false, precision = 38, scale = 2)
    private BigDecimal amount;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentTransactionStatus status;
    @ManyToOne
    @JoinColumn(nullable = false)
    private CurrencyAccount source;
    @ManyToOne
    private CurrencyAccount destination;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "paymentTransaction")
    private List<Refund> refunds;
    private String note;
}
