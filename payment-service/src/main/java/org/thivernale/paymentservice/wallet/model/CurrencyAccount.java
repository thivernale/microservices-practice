package org.thivernale.paymentservice.wallet.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.thivernale.paymentservice.util.BaseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "currency_accounts")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public class CurrencyAccount extends BaseEntity {
    @Column(nullable = false)
    private BigDecimal balance;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CurrencyType currency;
    @ManyToOne
    @JoinColumn(nullable = false)
    private BankAccount bankAccount;
    @OneToMany(mappedBy = "source", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<PaymentTransaction> sourceTransactions = new ArrayList<>();
    @OneToMany(mappedBy = "destination", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<PaymentTransaction> destinationTransactions = new ArrayList<>();
}
