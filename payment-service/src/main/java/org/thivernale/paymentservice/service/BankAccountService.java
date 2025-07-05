package org.thivernale.paymentservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thivernale.paymentservice.model.BankAccount;
import org.thivernale.paymentservice.repository.BankAccountRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BankAccountService {
    private final BankAccountRepository bankAccountRepository;

    public Optional<BankAccount> findById(Long id) {
        return bankAccountRepository.findById(id);
    }
}
