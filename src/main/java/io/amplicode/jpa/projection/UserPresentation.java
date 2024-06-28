package io.amplicode.jpa.projection;

/**
 * Projection for {@link io.amplicode.jpa.model.User}
 */
public interface UserPresentation {
    Long getId();

    String getUsername();
}