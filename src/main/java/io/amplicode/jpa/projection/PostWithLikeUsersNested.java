package io.amplicode.jpa.projection;

import io.amplicode.jpa.model.Post;

import java.util.List;

/**
 * Projection for {@link Post}
 */
public interface PostWithLikeUsersNested {
    Long getId();

    String getSlug();

    String getTitle();

    List<UserPresentation> getLikeUsers();
}