package io.amplicode.jpa.repository;

import io.amplicode.jpa.TestUtils;
import io.amplicode.jpa.model.Post;
import io.amplicode.jpa.model.Post_;
import io.amplicode.jpa.model.User;
import io.amplicode.jpa.model.User_;
import io.amplicode.jpa.projection.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static io.amplicode.jpa.InitTestDataService.POST1_SLUG;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@SpringBootTest
@ActiveProfiles("postgres")
public class ToOneTest {

    @Autowired
    private PostRepository postRepository;

    @PersistenceContext
    private EntityManager em;

    /**
     * Также ни каких проблем, в запросе присутствуют только те колонки, которые указанны в Projection.
     * Также в этом случе проекция это прокси во круг TupleBackedMap
     */
    @Test
    void derivedMethodInterfaceFlatPrj() {
        List<PostWithAuthorFlat> articles = postRepository.findAllByTitleContainsIgnoreCase("tcp", PostWithAuthorFlat.class);
        assertEquals(1, articles.size());
        PostWithAuthorFlat articleFirst = articles.getFirst();
        assertEquals(POST1_SLUG, articleFirst.getSlug());
        assertEquals("Maksim Esteban", articleFirst.getAuthorUsername());
    }

    /**
     * Здесь начинаются проблемы. Для базовых полей первого уровня, загружается только то что указанно в проекции, т.е.
     * id, slug, tittle, а вот для вложенного объекта в запросе были подставлены все колонки.
     * <a href="https://github.com/spring-projects/spring-data-jpa/issues/3352">Данная проблема известна</a> и имеет офф. ответ.
     * Обратим внимание, что ArticleWithAuthorNested это все также прокси вокруг TupleBackedMap, а вот вложенный объект
     * UserPresentation это прокси вокруг самой сущности User.
     */
    @Test
    void derivedMethodInterfaceNestedPrj() throws Exception {
        List<PostWithAuthorNested> articles = postRepository.findAllByTitleContainsIgnoreCase("tcp", PostWithAuthorNested.class);
        assertEquals(1, articles.size());
        PostWithAuthorNested articleFirst = articles.getFirst();
        Object targetObject = TestUtils.getProxyTargetObject(articleFirst);
        assertEquals("org.springframework.data.jpa.repository.query.AbstractJpaQuery$TupleConverter$TupleBackedMap", targetObject.getClass().getName());
        assertEquals(POST1_SLUG, articleFirst.getSlug());
        UserPresentation author = articleFirst.getAuthor();
        assertInstanceOf(User.class, TestUtils.getProxyTargetObject(author));
        assertEquals("Maksim Esteban", author.getUsername());
    }

    /**
     * Работает, грузится только то что надо. Ни каких прокси. Spring Data JPA передает result type в hibernate и он уже спокойно мапит.
     */
    @Test
    void derivedMethodClassFlatPrj() {
        var articles = postRepository.findAllByTitleContainsIgnoreCase("tcp", PostWithAuthorFlatDto.class);
        assertEquals(1, articles.size());
        var articleFirst = articles.getFirst();
        assertEquals(POST1_SLUG, articleFirst.slug());
        assertEquals("Maksim Esteban", articleFirst.authorUsername());
    }

    /**
     * Это не возможно, явно получаем ошибку `Cannot set field 'authorMap' to instantiate 'io.amplicode.jpa.projection.ArticleWithAuthorNestedDto`
     * о чем явно сказано в документации - https://docs.spring.io/spring-data/jpa/reference/repositories/projections.html#projections.dtos
     * These DTO types can be used in exactly the same way projection interfaces are used, except that no proxying happens and no nested projections can be applied.
     * <p>
     * На самом деле, вся эта логика лежит на стороне Hibernate. Т.е. Spring Data тут ни при чем
     */
    @Test
    void derivedMethodClassNestedPrj() {
        List<PostWithAuthorNestedDto> articles = postRepository.findAllByTitleContainsIgnoreCase("tcp", PostWithAuthorNestedDto.class);
        assertEquals(1, articles.size());
        var articleFirst = articles.getFirst();
        assertEquals(POST1_SLUG, articleFirst.slug());
        assertEquals("Maksim Esteban", articleFirst.author().username());
    }

    /**
     * А вот если в качестве nested использовать entity, то все будет хорошо, за исключением того что nested объект будет выгружен целиком.
     * Ну хоть какая-то оптимизация на первом уровне.
     */
    @Test
    void derivedMethodClassNestedEntity() {
        List<PostWithAuthorEntity> articles = postRepository.findAllByTitleContainsIgnoreCase("tcp", PostWithAuthorEntity.class);
        assertEquals(1, articles.size());
        var articleFirst = articles.getFirst();
        assertEquals(POST1_SLUG, articleFirst.slug());
        assertEquals("Maksim Esteban", articleFirst.author().getUsername());
    }

    /**
     * Все по красоте, загружаем только то что нужно. Под интерфейсом прокси с нашим любимым TupleBackedMap
     */
    @Test
    void queryMethodInterfaceFlatPrj() throws Exception {
        List<PostWithAuthorFlat> articles = postRepository.findAllPostWithAuthorFlat("tcp");
        assertEquals(1, articles.size());
        PostWithAuthorFlat article = articles.getFirst();
        Object targetObject = TestUtils.getProxyTargetObject(article);
        assertEquals("org.springframework.data.jpa.repository.query.AbstractJpaQuery$TupleConverter$TupleBackedMap", targetObject.getClass().getName());
        assertEquals("Ill-quantify-the-redundant-TCP-bus-that-should-hard-drive-the-ADP-bandwidth!-553", article.getSlug());
        assertEquals("Maksim Esteban", article.getAuthorUsername());
    }

    /**
     * И так мы написали запрос. Дали алиасы и ожидаем что произойдет какая-то магия authorUsername будет превращен в еще один прокси Author,
     * но так не будет. Там все тот же прямой TupleBackedMap. Ты ему ключ, он тебе значение и для authorMap у него ничего нет.
     */
    @Test
    void queryMethodInterfaceNestedPrj() throws Exception {
        var articles = postRepository.findAllPostWithAuthorNested("tcp");
        assertEquals(1, articles.size());
        PostWithAuthorNested article = articles.getFirst();
        Object targetObject = TestUtils.getProxyTargetObject(article);
        assertEquals("org.springframework.data.jpa.repository.query.AbstractJpaQuery$TupleConverter$TupleBackedMap", targetObject.getClass().getName());
        assertEquals("Ill-quantify-the-redundant-TCP-bus-that-should-hard-drive-the-ADP-bandwidth!-553", article.getSlug());
        assertEquals("Maksim Esteban", article.getAuthor().getUsername());
    }

    /**
     * Тут уже начинается содомия. Начинаем мешать интерфейс проекции с DTO.
     * А самое главное здесь уже под прокси будет прятаться не TupleBackedMap, HashMap.
     * Давайте выясним откуда он взялся. Проекция создается тута org.springframework.data.projection.ProxyProjectionFactory#createProjection(java.lang.Class, java.lang.Object)
     */
    @Test
    void queryMethodInterfaceNestedClass() throws Exception {
        var articles = postRepository.findAllPostWithAuthorClass("tcp");
        assertEquals(1, articles.size());
        var article = articles.getFirst();
        Object targetObject = TestUtils.getProxyTargetObject(article);
        assertEquals("java.util.HashMap", targetObject.getClass().getName());
        assertEquals("Ill-quantify-the-redundant-TCP-bus-that-should-hard-drive-the-ADP-bandwidth!-553", article.getSlug());
        assertEquals("Maksim Esteban", article.getAuthor().username());
    }

    @Test
    void queryMethodClassFlat() {
        var articles = postRepository.findAllPostWithAuthorFlatDto("tcp");
        assertEquals(1, articles.size());
        PostWithAuthorFlatDto article = articles.getFirst();
        assertEquals("Ill-quantify-the-redundant-TCP-bus-that-should-hard-drive-the-ADP-bandwidth!-553", article.slug());
        assertEquals("Maksim Esteban", article.authorUsername());
    }

    @Test
    void queryMethodClassNested() {
        var articles = postRepository.findAllPostWithAuthorNestedDto("tcp");
        assertEquals(1, articles.size());
        PostWithAuthorNestedDto article = articles.getFirst();
        assertEquals("Ill-quantify-the-redundant-TCP-bus-that-should-hard-drive-the-ADP-bandwidth!-553", article.slug());
        assertEquals("Maksim Esteban", article.author().username());
    }

    @Test
    void queryMethodClassNestedMap() {
        var articles = postRepository.findAllPostWithAuthorNestedMap("tcp");
        assertEquals(1, articles.size());
        var article = articles.getFirst();
        assertEquals("Ill-quantify-the-redundant-TCP-bus-that-should-hard-drive-the-ADP-bandwidth!-553", article.slug());
        assertEquals("Maksim Esteban", article.authorMap().get(User_.USERNAME));
    }

    @Test
    void criteriaTuple() {
        var cb = em.getCriteriaBuilder();
        var query = cb.createTupleQuery();

        var owner = query.from(Post.class);

        var idPath = owner.<Long>get(Post_.ID);
        var slugPath = owner.<String>get(Post_.SLUG);
        var titlePath = owner.<String>get(Post_.TITLE);
        var authorIdPath = owner.get(Post_.AUTHOR).<Long>get(User_.ID);
        var authorUsernamePath = owner.get(Post_.AUTHOR).<String>get(User_.USERNAME);

        query.select(cb.tuple(idPath, slugPath, titlePath, authorIdPath, authorUsernamePath))
                .where(cb.like(cb.lower(titlePath), "%tcp%"));

        var resultList = em.createQuery(query).getResultList().stream()
                .map(tuple -> new PostWithAuthorNestedDto(
                        tuple.get(idPath),
                        tuple.get(slugPath),
                        tuple.get(titlePath),
                        new UserPresentationDto(
                                tuple.get(authorIdPath),
                                tuple.get(authorUsernamePath)
                        )
                )).toList();

        for (PostWithAuthorNestedDto post : resultList) {
            assertEquals(POST1_SLUG, post.slug());
            assertEquals("Maksim Esteban", post.author().username());
        }
    }
}
