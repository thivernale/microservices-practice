package org.thivernale.customerservice.dto;

import org.thivernale.customerservice.model.Address;

public record CustomerResponse(
    String id,
    String firstName,
    String lastName,
    String email,
    Address address) {
}
