package io.amplicode.jpa.projection;

/**
 * DTO for {@link io.amplicode.jpa.model.Article}
 */
public record ArticleBasicDto(Long id, String slug, String title) {
}