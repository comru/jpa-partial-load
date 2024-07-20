package io.amplicode.jpa.repository;

import io.amplicode.jpa.model.Post_;
import io.amplicode.jpa.projection.*;
import jakarta.persistence.Tuple;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class PostRepositoryQueryMethodTest {

    @Autowired
    private PostRepository postRepository;

    @Test
    void interfaceBasic() {
        List<PostBasic> articles = postRepository.findAllPostBase("tcp");
        assertEquals(1, articles.size());

        PostBasic postBasic = articles.get(0);
        assertEquals("Ill-quantify-the-redundant-TCP-bus-that-should-hard-drive-the-ADP-bandwidth!-553", postBasic.getSlug());
    }

    @Test
    void interfaceToOneFlat() {
        List<PostWithAuthorFlat> articles = postRepository.findAllPostWithAuthorFlat("tcp");
        assertEquals(1, articles.size());

        PostWithAuthorFlat article = articles.get(0);
        assertEquals("Ill-quantify-the-redundant-TCP-bus-that-should-hard-drive-the-ADP-bandwidth!-553", article.getSlug());
        assertEquals("Maksim Esteban", article.getAuthorUsername());
    }

    @Test
    void interfaceToOneNested() {
        var articles = postRepository.findAllPostWithAuthorNested("tcp");
        assertEquals(1, articles.size());

        PostWithAuthorNested article = articles.get(0);
        assertEquals("Ill-quantify-the-redundant-TCP-bus-that-should-hard-drive-the-ADP-bandwidth!-553", article.getSlug());
        assertEquals("Maksim Esteban", article.getAuthor().getUsername());
    }

    @Test
    void interfaceToMany() {
        var articles = postRepository.findAllPostWithAuthorNested("tcp");
        assertEquals(1, articles.size());

        PostWithAuthorNested article = articles.get(0);
        assertEquals("Ill-quantify-the-redundant-TCP-bus-that-should-hard-drive-the-ADP-bandwidth!-553", article.getSlug());
        assertEquals("Maksim Esteban", article.getAuthor().getUsername());
    }

    @Test
    void classBasic() {
        var articles = postRepository.findAllPostBasicDto("tcp");
        assertEquals(1, articles.size());

        PostBasicDto articleBasic = articles.get(0);
        assertEquals("Ill-quantify-the-redundant-TCP-bus-that-should-hard-drive-the-ADP-bandwidth!-553", articleBasic.slug());
    }

    @Test
    void classToOneFlat() {
        var articles = postRepository.findAllPostWithAuthorFlatDto("tcp");
        assertEquals(1, articles.size());

        PostWithAuthorFlatDto article = articles.get(0);
        assertEquals("Ill-quantify-the-redundant-TCP-bus-that-should-hard-drive-the-ADP-bandwidth!-553", article.slug());
        assertEquals("Maksim Esteban", article.authorUsername());
    }

    @Test
    void classToOneNested() {
        var articles = postRepository.findAllPostWithAuthorNestedDto("tcp");
        assertEquals(1, articles.size());

        PostWithAuthorNestedDto article = articles.get(0);
        assertEquals("Ill-quantify-the-redundant-TCP-bus-that-should-hard-drive-the-ADP-bandwidth!-553", article.slug());
        assertEquals("Maksim Esteban", article.author().username());
    }

    @Test
    void entityBasic() {
        var articles = postRepository.findAllEntityBasic("tcp");
        assertEquals(1, articles.size());

        var articleBasic = articles.get(0);
        assertEquals("Ill-quantify-the-redundant-TCP-bus-that-should-hard-drive-the-ADP-bandwidth!-553", articleBasic.getSlug());
    }

    @Test
    void tupleTupleBasic() {
        var articles = postRepository.findAllTupleBasic("tcp");
        assertEquals(1, articles.size());

        Tuple article = articles.get(0);
        assertEquals("Ill-quantify-the-redundant-TCP-bus-that-should-hard-drive-the-ADP-bandwidth!-553", article.get(Post_.SLUG));
    }

    @Test
    void tupleToOne() {
        var articles = postRepository.findAllTupleWithAuthor("tcp");
        assertEquals(1, articles.size());

        var article = articles.get(0);
        assertEquals("Ill-quantify-the-redundant-TCP-bus-that-should-hard-drive-the-ADP-bandwidth!-553", article.get(Post_.SLUG));
        assertEquals("Maksim Esteban", article.get("authorUsername"));
    }

    @Test
    void tupleToMany() {
        var articles = postRepository.findAllWithFavorited("tcp");
        assertEquals(1, articles.size());

        var article = articles.get(0);
        assertEquals("Ill-quantify-the-redundant-TCP-bus-that-should-hard-drive-the-ADP-bandwidth!-553", article.get(Post_.SLUG));
//        assertEquals("Maksim Esteban", article.get("authorUsername"));
    }

    @Test
    void objectBasic() {
        var articles = postRepository.findAllObjectArrayBasic("tcp");
        assertEquals(1, articles.size());

        var article = articles.get(0);
        assertEquals("Ill-quantify-the-redundant-TCP-bus-that-should-hard-drive-the-ADP-bandwidth!-553", article[1]);
    }

    @Test
    void objectToOne() {
        var articles = postRepository.findAllTupleWithAuthor("tcp");
        assertEquals(1, articles.size());

        var article = articles.get(0);
        assertEquals("Ill-quantify-the-redundant-TCP-bus-that-should-hard-drive-the-ADP-bandwidth!-553", article.get(Post_.SLUG));
        assertEquals("Maksim Esteban", article.get("authorUsername"));
    }
}