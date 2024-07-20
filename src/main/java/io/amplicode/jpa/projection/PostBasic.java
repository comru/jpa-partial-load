package io.amplicode.jpa.projection;

import io.amplicode.jpa.model.Post;

/**
 * Projection for {@link Post}
 */
public interface PostBasic {
    Long getId();
    String getSlug();
    String getTitle();
}