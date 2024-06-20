package io.amplicode.jpa;

/**
 * Projection for {@link Owner}
 */
public interface OwnerProjection {
    Long getId();

    String getFirstName();

    String getLastName();
}