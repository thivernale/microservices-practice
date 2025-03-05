package org.thivernale.customerservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.thivernale.customerservice.dto.CustomerRequest;
import org.thivernale.customerservice.dto.CustomerResponse;
import org.thivernale.customerservice.service.CustomerService;

import java.util.List;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<String> createCustomer(@RequestBody @Valid CustomerRequest customerRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(customerService.createCustomer(customerRequest));
    }

    @PutMapping("{id}")
    public ResponseEntity<Void> updateCustomer(
        @RequestBody @Valid CustomerRequest customerRequest,
        @PathVariable("id") String id
    ) {
        customerService.updateCustomer(id, customerRequest);
        return ResponseEntity.accepted()
            .build();
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponse>> findAll() {
        return ResponseEntity.ok(customerService.findAll());
    }

    @GetMapping("/exists/{id}")
    public ResponseEntity<Boolean> existsById(@PathVariable("id") String id) {
        return ResponseEntity.ok(customerService.existsById(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> findById(@PathVariable("id") String id) {
        return ResponseEntity.ok(customerService.findById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable("id") String id) {
        customerService.deleteById(id);
        return ResponseEntity.accepted()
            .build();
    }
}
