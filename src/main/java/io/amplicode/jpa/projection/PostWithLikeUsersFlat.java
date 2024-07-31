package io.amplicode.jpa.projection;

import io.amplicode.jpa.model.Post;

/**
 * Projection for {@link Post}
 */
public interface PostWithLikeUsersFlat {
    Long getId();

    String getSlug();

    String getTitle();

    Long getLikeUsersId();

    String getLikeUsersUsername();
}