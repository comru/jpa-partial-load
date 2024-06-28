package io.amplicode.jpa.projection;

import io.amplicode.jpa.model.User;

import java.util.Set;

/**
 * DTO for {@link io.amplicode.jpa.model.Article}
 */
public record ArticleWithFavoritedDto(Long id,
                                      String slug,
                                      String title,
                                      Set<User> favorited) {
}