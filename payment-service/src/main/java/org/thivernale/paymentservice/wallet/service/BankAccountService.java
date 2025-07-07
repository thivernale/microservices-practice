package org.thivernale.paymentservice.wallet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thivernale.paymentservice.wallet.model.BankAccount;
import org.thivernale.paymentservice.wallet.repository.BankAccountRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BankAccountService {
    private final BankAccountRepository bankAccountRepository;

    public Optional<BankAccount> findById(Long id) {
        return bankAccountRepository.findById(id);
    }

    public BankAccount save(BankAccount bankAccount) {
        return bankAccountRepository.save(bankAccount);
    }
}
