package io.amplicode.jpa.projection;

/**
 * Projection for {@link io.amplicode.jpa.model.Article}
 */
public interface ArticleWithAuthorFlat {
    Long getId();

    String getSlug();

    String getTitle();

    Long getAuthorId();

    String getAuthorUsername();
}