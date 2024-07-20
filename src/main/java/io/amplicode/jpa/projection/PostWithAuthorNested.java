package io.amplicode.jpa.projection;

import io.amplicode.jpa.model.Post;

/**
 * Projection for {@link Post}
 */
public interface PostWithAuthorNested {
    Long getId();

    String getSlug();

    String getTitle();

    UserPresentation getAuthor();
}