package io.amplicode.jpa.projection;

import io.amplicode.jpa.model.Post;

/**
 * DTO for {@link Post}
 */
public record PostBasicDto(Long id, String slug, String title) {
}