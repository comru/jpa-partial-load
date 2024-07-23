package io.amplicode.jpa.projection;

import io.amplicode.jpa.model.Post;

import java.util.Map;

/**
 * DTO for {@link Post}
 */
public record PostWithAuthorNestedMap(Long id,
                                      String slug,
                                      String title,
                                      Map<String, Object> authorMap) {
}