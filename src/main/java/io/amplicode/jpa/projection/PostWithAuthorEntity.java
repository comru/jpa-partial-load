package io.amplicode.jpa.projection;

import io.amplicode.jpa.model.Post;
import io.amplicode.jpa.model.User;

/**
 * DTO for {@link Post}
 */
public record PostWithAuthorEntity(Long id,
                                   String slug,
                                   String title,
                                   User author) {
}