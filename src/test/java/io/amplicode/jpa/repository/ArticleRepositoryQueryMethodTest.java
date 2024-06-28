package io.amplicode.jpa.repository;

import io.amplicode.jpa.model.Article_;
import io.amplicode.jpa.projection.*;
import jakarta.persistence.Tuple;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class ArticleRepositoryQueryMethodTest {

    @Autowired
    private ArticleRepository articleRepository;

    @Test
    void interfaceBasic() {
        List<ArticleBasic> articles = articleRepository.findAllArticleBase("tcp");
        assertEquals(1, articles.size());

        ArticleBasic articleBasic = articles.get(0);
        assertEquals("Ill-quantify-the-redundant-TCP-bus-that-should-hard-drive-the-ADP-bandwidth!-553", articleBasic.getSlug());
    }

    @Test
    void interfaceToOneFlat() {
        List<ArticleWithAuthorFlat> articles = articleRepository.findAllArticleWithAuthorFlat("tcp");
        assertEquals(1, articles.size());

        ArticleWithAuthorFlat article = articles.get(0);
        assertEquals("Ill-quantify-the-redundant-TCP-bus-that-should-hard-drive-the-ADP-bandwidth!-553", article.getSlug());
        assertEquals("Maksim Esteban", article.getAuthorUsername());
    }

    @Test
    void interfaceToOneNested() {
        var articles = articleRepository.findAllArticleWithAuthorNested("tcp");
        assertEquals(1, articles.size());

        ArticleWithAuthorNested article = articles.get(0);
        assertEquals("Ill-quantify-the-redundant-TCP-bus-that-should-hard-drive-the-ADP-bandwidth!-553", article.getSlug());
        assertEquals("Maksim Esteban", article.getAuthor().getUsername());
    }

    @Test
    void interfaceToMany() {
        var articles = articleRepository.findAllArticleWithAuthorNested("tcp");
        assertEquals(1, articles.size());

        ArticleWithAuthorNested article = articles.get(0);
        assertEquals("Ill-quantify-the-redundant-TCP-bus-that-should-hard-drive-the-ADP-bandwidth!-553", article.getSlug());
        assertEquals("Maksim Esteban", article.getAuthor().getUsername());
    }

    @Test
    void classBasic() {
        var articles = articleRepository.findAllArticleBasicDto("tcp");
        assertEquals(1, articles.size());

        ArticleBasicDto articleBasic = articles.get(0);
        assertEquals("Ill-quantify-the-redundant-TCP-bus-that-should-hard-drive-the-ADP-bandwidth!-553", articleBasic.slug());
    }

    @Test
    void classToOneFlat() {
        var articles = articleRepository.findAllArticleWithAuthorFlatDto("tcp");
        assertEquals(1, articles.size());

        ArticleWithAuthorFlatDto article = articles.get(0);
        assertEquals("Ill-quantify-the-redundant-TCP-bus-that-should-hard-drive-the-ADP-bandwidth!-553", article.slug());
        assertEquals("Maksim Esteban", article.authorUsername());
    }

    @Test
    void classToOneNested() {
        var articles = articleRepository.findAllArticleWithAuthorNestedDto("tcp");
        assertEquals(1, articles.size());

        ArticleWithAuthorNestedDto article = articles.get(0);
        assertEquals("Ill-quantify-the-redundant-TCP-bus-that-should-hard-drive-the-ADP-bandwidth!-553", article.slug());
        assertEquals("Maksim Esteban", article.user().username());
    }

    @Test
    void tupleTupleBasic() {
        var articles = articleRepository.findAllTupleBasic("tcp");
        assertEquals(1, articles.size());

        Tuple article = articles.get(0);
        assertEquals("Ill-quantify-the-redundant-TCP-bus-that-should-hard-drive-the-ADP-bandwidth!-553", article.get(Article_.SLUG));
    }

    @Test
    void tupleToOne() {
        var articles = articleRepository.findAllTupleWithAuthor("tcp");
        assertEquals(1, articles.size());

        var article = articles.get(0);
        assertEquals("Ill-quantify-the-redundant-TCP-bus-that-should-hard-drive-the-ADP-bandwidth!-553", article.get(Article_.SLUG));
        assertEquals("Maksim Esteban", article.get("authorUsername"));
    }

    @Test
    void tupleToMany() {
        var articles = articleRepository.findAllWithFavorited("tcp");
        assertEquals(1, articles.size());

        var article = articles.get(0);
        assertEquals("Ill-quantify-the-redundant-TCP-bus-that-should-hard-drive-the-ADP-bandwidth!-553", article.get(Article_.SLUG));
//        assertEquals("Maksim Esteban", article.get("authorUsername"));
    }

    @Test
    void objectBasic() {
        var articles = articleRepository.findAllObjectBasic("tcp");
        assertEquals(1, articles.size());

        var article = articles.get(0);
        assertEquals("Ill-quantify-the-redundant-TCP-bus-that-should-hard-drive-the-ADP-bandwidth!-553", article[1]);
    }

    @Test
    void objectToOne() {
        var articles = articleRepository.findAllTupleWithAuthor("tcp");
        assertEquals(1, articles.size());

        var article = articles.get(0);
        assertEquals("Ill-quantify-the-redundant-TCP-bus-that-should-hard-drive-the-ADP-bandwidth!-553", article.get(Article_.SLUG));
        assertEquals("Maksim Esteban", article.get("authorUsername"));
    }
}