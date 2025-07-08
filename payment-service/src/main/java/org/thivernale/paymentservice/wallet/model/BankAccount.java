package org.thivernale.paymentservice.wallet.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.thivernale.paymentservice.util.BaseEntity;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "bank_accounts")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public class BankAccount extends BaseEntity {
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "bankAccount")
    List<CurrencyAccount> currencyAccounts;
    private String number;
    private String customerId;
    @Column(nullable = false)
    private BigDecimal balance;
    @Column(nullable = false)
    private String currency;
}
