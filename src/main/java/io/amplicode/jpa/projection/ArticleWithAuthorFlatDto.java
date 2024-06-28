package io.amplicode.jpa.projection;

/**
 * DTO for {@link io.amplicode.jpa.model.Article}
 */
public record ArticleWithAuthorFlatDto(Long id,
                                       String slug,
                                       String title,
                                       Long authorId,
                                       String authorUsername) {
}