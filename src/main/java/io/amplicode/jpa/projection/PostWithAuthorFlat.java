package io.amplicode.jpa.projection;

import io.amplicode.jpa.model.Post;

/**
 * Projection for {@link Post}
 */
public interface PostWithAuthorFlat {
    Long getId();

    String getSlug();

    String getTitle();

    Long getAuthorId();

    String getAuthorUsername();
}