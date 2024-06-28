package io.amplicode.jpa.projection;

import java.util.List;

/**
 * Projection for {@link io.amplicode.jpa.model.Article}
 */
public interface ArticleWithFavoritedNested {
    Long getId();

    String getSlug();

    String getTitle();

    List<UserPresentation> getFavorited();
}