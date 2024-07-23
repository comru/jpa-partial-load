package io.amplicode.jpa.projection;

import io.amplicode.jpa.model.Post;

/**
 * DTO for {@link Post}
 */
public record PostWithLikeUsersDto(Long id,
                                   String slug,
                                   String title,
                                   Long likeUsersId,
                                   String likeUsersUsername) {
}