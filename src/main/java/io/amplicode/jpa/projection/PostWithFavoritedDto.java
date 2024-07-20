package io.amplicode.jpa.projection;

import io.amplicode.jpa.model.Post;

/**
 * DTO for {@link Post}
 */
public record PostWithFavoritedDto(Long id,
                                   String slug,
                                   String title,
                                   Object likeUsers) {
}