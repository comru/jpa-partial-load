package io.amplicode.jpa.projection;

import io.amplicode.jpa.model.Post;

/**
 * DTO for {@link Post}
 */
public record PostWithAuthorFlatDto(Long id,
                                    String slug,
                                    String title,
                                    Long authorId,
                                    String authorUsername) {
}