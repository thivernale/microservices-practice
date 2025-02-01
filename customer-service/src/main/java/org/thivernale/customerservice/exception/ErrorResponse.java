package org.thivernale.customerservice.exception;

import java.util.Map;

public record ErrorResponse(Map<String, String> errors) {
}
