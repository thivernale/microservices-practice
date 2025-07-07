package org.thivernale.paymentservice.payment.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

@Validated
public record Customer(
    String id,
    @NotNull(message = "Customer first name is required")
    String firstName,
    @NotNull(message = "Customer last name is required")
    String lastName,
    @NotNull(message = "Customer email is required")
    @Email(message = "Customer email is not a valid email address")
    String email
) {
}
