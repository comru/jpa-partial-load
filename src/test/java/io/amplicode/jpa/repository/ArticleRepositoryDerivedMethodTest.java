package io.amplicode.jpa.repository;

import io.amplicode.jpa.model.Article;
import io.amplicode.jpa.model.User;
import io.amplicode.jpa.projection.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class ArticleRepositoryDerivedMethodTest {

    @Autowired
    private ArticleRepository articleRepository;

    @Test
    void fullLoad() {
        List<Article> all = articleRepository.findAllWithAssociations();
        for (Article article : all) {
            assertNotNull(article.getTitle());
            assertNotNull(article.getSlug());
            assertNotNull(article.getBody());
            assertNotNull(article.getCreatedAt());
            assertNotNull(article.getUpdatedAt());

            User author = article.getAuthor();
            assertNotNull(author);
            assertNotNull(author.getId());

            assertEquals(2, article.getFavorited().size());
            assertEquals(4, article.getTags().size());
        }
    }

    @Test
    void interfaceBasic() {
        List<ArticleBasic> articles = articleRepository.findAllByTitleContainsIgnoreCase("tcp", ArticleBasic.class);
        assertEquals(1, articles.size());
    }

    @Test
    void interfaceToOneNested() {
        List<ArticleWithAuthorNested> tcp = articleRepository.findAllByTitleContainsIgnoreCase("tcp", ArticleWithAuthorNested.class);
        assertEquals(1, tcp.size());
    }

    @Test
    void interfaceToOneFlat() {
        List<ArticleWithAuthorFlat> articles = articleRepository.findAllByTitleContainsIgnoreCase("tcp", ArticleWithAuthorFlat.class);
        assertEquals(1, articles.size());
    }

    @Test
    void interfaceProjectionToManyNested() {
        List<ArticleWithFavoritedNested> articles = articleRepository.findAllWithFavoritedByTitleContainsIgnoreCase("tcp", ArticleWithFavoritedNested.class);
        assertEquals(1, articles.size());
        var article = articles.get(0);
        assertEquals("Ill-quantify-the-redundant-TCP-bus-that-should-hard-drive-the-ADP-bandwidth!-553", article.getSlug());
        List<UserPresentation> favorited = article.getFavorited();
        assertEquals(2, favorited.size());
        assertEquals("Maksim Esteban", favorited.get(0).getUsername());
        assertEquals("Ping Sokołowski", favorited.get(1).getUsername());
    }

    @Test
    void classBasic() {
        List<ArticleBasicDto> articles = articleRepository.findAllByTitleContainsIgnoreCase("tcp", ArticleBasicDto.class);
        assertEquals(1, articles.size());
    }

    @Test
    void classBasicToOneFlat() {
        var articles = articleRepository.findAllByTitleContainsIgnoreCase("tcp", ArticleWithAuthorFlatDto.class);
        assertEquals(1, articles.size());
    }


}