package org.thivernale.customerservice.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thivernale.customerservice.dto.CustomerRequest;
import org.thivernale.customerservice.dto.CustomerResponse;
import org.thivernale.customerservice.exception.CustomerNotFoundException;
import org.thivernale.customerservice.exception.EmailAlreadyExistsException;
import org.thivernale.customerservice.grpc.BillingServiceGrpcClient;
import org.thivernale.customerservice.model.Customer;
import org.thivernale.customerservice.notification.NotificationProducer;
import org.thivernale.customerservice.repository.CustomerRepository;

import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final BillingServiceGrpcClient billingServiceGrpcClient;
    private final NotificationProducer notificationProducer;

    public String createCustomer(CustomerRequest customerRequest) {
        if (customerRepository.existsByEmail(customerRequest.email())) {
            throw new EmailAlreadyExistsException("Customer with email already exists: " + customerRequest.email());
        }

        Customer customer = customerRepository.save(customerMapper.toCustomer(customerRequest));

        billingServiceGrpcClient.createBillingAccount(
            customer.getId(),
            customer.getFirstName() + " " + customer.getLastName(),
            customer.getEmail());

        notificationProducer.sendNotification(customer);

        return customer
            .getId();
    }

    public void updateCustomer(String id, CustomerRequest customerRequest) {
        Customer customer = getCustomerById(id);

        mergeCustomer(customer, customerRequest);

        customerRepository.save(customer);
    }

    private void mergeCustomer(Customer customer, CustomerRequest customerRequest) {
        if (StringUtils.isNotBlank(customerRequest.firstName())) {
            customer.setFirstName(customerRequest.firstName());
        }
        if (StringUtils.isNotBlank(customerRequest.lastName())) {
            customer.setLastName(customerRequest.lastName());
        }
        if (StringUtils.isNotBlank(customerRequest.email())) {
            customer.setEmail(customerRequest.email());
        }
        if (customerRequest.address() != null) {
            customer.setAddress(customerRequest.address());
        }
    }

    public List<CustomerResponse> findAll() {
        return customerRepository.findAll()
            .stream()
            .map(customerMapper::fromCustomer)
            .toList();
    }

    public boolean existsById(String id) {
        return customerRepository.existsById(id);
    }

    public CustomerResponse findById(String id) {
        return customerMapper.fromCustomer(getCustomerById(id));
    }

    private Customer getCustomerById(String id) {
        return customerRepository.findById(id)
            .orElseThrow(() -> new CustomerNotFoundException(String.format("Cannot find customer with id %s",
                id)));
    }

    public void deleteById(String id) {
        customerRepository.deleteById(id);
    }
}
