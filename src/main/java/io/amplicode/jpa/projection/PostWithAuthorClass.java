package io.amplicode.jpa.projection;

import io.amplicode.jpa.model.Post;

import java.util.Map;

/**
 * Projection for {@link Post}
 */
public interface PostWithAuthorClass {
    Long getId();

    String getSlug();

    String getTitle();

    UserPresentationDto getAuthor();
}