package io.amplicode.jpa.projection;

/**
 * Projection for {@link io.amplicode.jpa.model.Article}
 */
public interface ArticleWithAuthorNested {
    Long getId();

    String getSlug();

    String getTitle();

    UserPresentation getAuthor();
}