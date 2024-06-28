package io.amplicode.jpa.projection;

import io.amplicode.jpa.model.Owner;

/**
 * Projection for {@link Owner}
 */
public interface OwnerProjection {
    Long getId();

    String getFirstName();

    String getLastName();
}