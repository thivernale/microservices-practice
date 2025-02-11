package org.thivernale.productservice.dto;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * DTO for {@link org.thivernale.productservice.model.Category}
 */
public record CategoryResponse(BigInteger id, String name, String description) implements Serializable {
}
