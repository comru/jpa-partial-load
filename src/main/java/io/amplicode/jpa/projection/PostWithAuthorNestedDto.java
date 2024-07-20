package io.amplicode.jpa.projection;

import io.amplicode.jpa.model.Post;

/**
 * DTO for {@link Post}
 */
public record PostWithAuthorNestedDto(Long id,
                                      String slug,
                                      String title,
                                      UserPresentationDto author) {
}