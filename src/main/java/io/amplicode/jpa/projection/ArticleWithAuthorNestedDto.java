package io.amplicode.jpa.projection;

/**
 * DTO for {@link io.amplicode.jpa.model.Article}
 */
public record ArticleWithAuthorNestedDto(Long id,
                                         String slug,
                                         String title,
                                         UserPresentationDto user) {
}