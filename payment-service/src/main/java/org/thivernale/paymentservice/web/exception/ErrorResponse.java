package org.thivernale.paymentservice.web.exception;

import java.util.Map;

public record ErrorResponse(Map<String, String> errors) {
}
