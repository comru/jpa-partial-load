package io.amplicode.jpa.projection;

import io.amplicode.jpa.model.Owner;

/**
 * DTO for {@link Owner}
 */
public record OwnerDto(Long id, String firstName, String lastName) {
}