package org.thivernale.paymentservice.exchangerates.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.thivernale.paymentservice.util.BaseEntity;
import org.thivernale.paymentservice.wallet.model.CurrencyType;

import java.math.BigDecimal;

@Entity
@Table(
    name = "exchange_rates",
    uniqueConstraints = {@UniqueConstraint(name = "uk_currency", columnNames = {"currency"})}
)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public class ExchangeRate extends BaseEntity {
    @Column(nullable = false, precision = 19, scale = 6)
    private BigDecimal rate;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CurrencyType currency;
}
