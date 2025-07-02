package org.thivernale.customerservice.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class EmailAlreadyExistsException extends RuntimeException {
    private final String message;
}
