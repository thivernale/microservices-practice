package org.thivernale.paymentservice.wallet.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.thivernale.paymentservice.wallet.model.PaymentTransaction;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(
    replace = AutoConfigureTestDatabase.Replace.NONE
)
class PaymentTransactionRepositoryTest {
    @Autowired
    private PaymentTransactionRepository paymentTransactionRepository;

    @Test
    void findByIdTest() {
        long id = 2L;
        Optional<PaymentTransaction> actual = paymentTransactionRepository.findOneById(id);
        assertThat(actual)
            .isPresent();
        assertThat(actual.get()
            .getId())
            .isEqualTo(id);
        assertThat(actual.get()
            .getRefunds())
            .isNotNull()
            .size()
            .isEqualTo(2);
    }
}
