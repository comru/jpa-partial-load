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

import static io.amplicode.jpa.InitTestDataService.POST1_AUTHOR_NAME;
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
        List<PostWithAuthorFlat> posts = postRepository.findAllByTitleContainsIgnoreCase("spring", PostWithAuthorFlat.class);
        assertEquals(1, posts.size());
        PostWithAuthorFlat postFirst = posts.getFirst();
        assertEquals(POST1_SLUG, postFirst.getSlug());
        assertEquals(POST1_AUTHOR_NAME, postFirst.getAuthorUsername());
    }

    /**
     * Здесь начинаются проблемы. Для базовых полей первого уровня, загружается только то что указанно в проекции, т.е.
     * id, slug, tittle, а вот для вложенного объекта в запросе были подставлены все колонки.
     * <a href="https://github.com/spring-projects/spring-data-jpa/issues/3352">Данная проблема известна</a> и имеет офф. ответ.
     * Обратим внимание, что postWithAuthorNested это все также прокси вокруг TupleBackedMap, а вот вложенный объект
     * UserPresentation это прокси вокруг самой сущности User.
     */
    @Test
    void derivedMethodInterfaceNestedPrj() throws Exception {
        List<PostWithAuthorNested> posts = postRepository.findAllByTitleContainsIgnoreCase("spring", PostWithAuthorNested.class);
        assertEquals(1, posts.size());
        PostWithAuthorNested postFirst = posts.getFirst();
        Object targetObject = TestUtils.getProxyTargetObject(postFirst);
        assertEquals("org.springframework.data.jpa.repository.query.AbstractJpaQuery$TupleConverter$TupleBackedMap", targetObject.getClass().getName());
        assertEquals(POST1_SLUG, postFirst.getSlug());
        UserPresentation author = postFirst.getAuthor();
        assertInstanceOf(User.class, TestUtils.getProxyTargetObject(author));
        assertEquals(POST1_AUTHOR_NAME, author.getUsername());
    }

    /**
     * Работает, грузится только то что надо. Ни каких прокси. Spring Data JPA передает result type в hibernate и он уже спокойно мапит.
     */
    @Test
    void derivedMethodClassFlatPrj() {
        var posts = postRepository.findAllByTitleContainsIgnoreCase("spring", PostWithAuthorFlatDto.class);
        assertEquals(1, posts.size());
        var postFirst = posts.getFirst();
        assertEquals(POST1_SLUG, postFirst.slug());
        assertEquals(POST1_AUTHOR_NAME, postFirst.authorUsername());
    }

    /**
     * Все по красоте, загружаем только то что нужно. Под интерфейсом прокси с нашим любимым TupleBackedMap
     */
    @Test
    void queryMethodInterfaceFlatPrj() throws Exception {
        List<PostWithAuthorFlat> posts = postRepository.findAllPostWithAuthorFlat("spring");
        assertEquals(1, posts.size());
        PostWithAuthorFlat post = posts.getFirst();
        Object targetObject = TestUtils.getProxyTargetObject(post);
        assertEquals("org.springframework.data.jpa.repository.query.AbstractJpaQuery$TupleConverter$TupleBackedMap", targetObject.getClass().getName());
        assertEquals(POST1_SLUG, post.getSlug());
        assertEquals(POST1_AUTHOR_NAME, post.getAuthorUsername());
    }

    /**
     * Тут уже начинается содомия. Возможно это самый изощренный и самый не задокументированный способ.
     * Начинаем мешать интерфейс проекции с DTO классами. Но самое главное, что оно работает, грузится только то что надо.
     * А самое главное здесь уже под прокси будет прятаться не TupleBackedMap, HashMap.
     * Давайте выясним откуда он взялся. Проекция создается тута org.springframework.data.projection.ProxyProjectionFactory#createProjection(java.lang.Class, java.lang.Object)
     */
    @Test
    void queryMethodInterfaceNestedClass() throws Exception {
        var posts = postRepository.findAllPostWithAuthorClass("spring");
        assertEquals(1, posts.size());
        var post = posts.getFirst();
        Object targetObject = TestUtils.getProxyTargetObject(post);
        assertEquals("java.util.HashMap", targetObject.getClass().getName());
        assertEquals(POST1_SLUG, post.getSlug());
        assertEquals(POST1_AUTHOR_NAME, post.getAuthor().username());
    }

    /**
     * Class-based projects они же DTO работают, как всегда, отлично! Ни какой магии, ну максимум чуть-чуть рефлексии.
     */
    @Test
    void queryMethodClassFlat() {
        var posts = postRepository.findAllPostWithAuthorFlatDto("spring");
        assertEquals(1, posts.size());
        PostWithAuthorFlatDto post = posts.getFirst();
        assertEquals(POST1_SLUG, post.slug());
        assertEquals(POST1_AUTHOR_NAME, post.authorUsername());
    }

    /**
     * Хотите nested, получайте. Мы же в HQL находимся, можем инициализировать нашу DTO как нам удобно.
     * В том числе и создавая новые DTO объекты внутри DTO
     */
    @Test
    void queryMethodClassNested() {
        var posts = postRepository.findAllPostWithAuthorNestedDto("spring");
        assertEquals(1, posts.size());
        PostWithAuthorNestedDto post = posts.getFirst();
        assertEquals(POST1_SLUG, post.slug());
        assertEquals(POST1_AUTHOR_NAME, post.author().username());
    }

    /**
     * Можем не только DTO внутрь DTO засунуть, но и Map или List
     */
    @Test
    void queryMethodClassNestedMap() {
        var posts = postRepository.findAllPostWithAuthorNestedMap("spring");
        assertEquals(1, posts.size());
        var post = posts.getFirst();
        assertEquals(POST1_SLUG, post.slug());
        assertEquals(POST1_AUTHOR_NAME, post.authorMap().get(User_.USERNAME));
    }

    @Test
    void queryMethodTuple() {
        var posts = postRepository.findAllTupleWithAuthor("spring");
        assertEquals(1, posts.size());
        var post = posts.getFirst();
        assertEquals(POST1_SLUG, post.get(Post_.SLUG));
        assertEquals(POST1_AUTHOR_NAME, post.get("authorUsername"));
    }

    /**
     * Почти то же самое что tuple, только Map.
     */
    @Test
    void queryMethodMap() {
        var posts = postRepository.findAllMapWithAuthor("spring");
        assertEquals(1, posts.size());
        var post = posts.getFirst();
        assertEquals(POST1_SLUG, post.get("slug"));
        assertEquals(POST1_AUTHOR_NAME, post.get("authorUsername"));
    }

    /**
     * Скажем так, на любителя. Если возвращаем две три колонки, наверно еще ок, но даже в нашем случае,
     * вероятность ошибиться с индексом велика.
     */
    @Test
    void queryMethodObjectArray() {
        var posts = postRepository.findAllObjectWithAuthor("spring");
        assertEquals(1, posts.size());
        var post = posts.getFirst();
        assertEquals(POST1_SLUG, post[1]);
        assertEquals(POST1_AUTHOR_NAME, post[4]);
    }

    /**
     * Тоже самое, что Object[], только List
     */
    @Test
    void queryMethodList() {
        var posts = postRepository.findAllListWithAuthor("spring");
        assertEquals(1, posts.size());
        var post = posts.getFirst();
        assertEquals(POST1_SLUG, post.get(1));
        assertEquals(POST1_AUTHOR_NAME, post.get(4));
    }

    /**
     * Чем интересен данный кейс:
     * 1. Мы используем Criteria API, который является "type-safe alternative to HQL"
     * 2. Для формирования возвращаемы значений мы используем Tuple. Атрибуты которые мы хотим получить мы указываем как Path
     * <code>
     * owner.<String>get(Post_.SLUG)
     * </code>
     * 3. Имя атрибута мы указываем с помощью константы, которая была сгенерирована автоматически с
     * использованием зависимости org.hibernate:hibernate-jpamodelgen. Как уже и писал ранее,
     * в последней документации hibernate данный способ используется во всех примерах, можно сказать, что это тихая рекомендация.
     * 4. Затем на основе этих же jakarta.persistence.criteria.Path мы достаем значения из Tuple и складываем в наше DTO
     * или можем проводить с ними какие-то манипуляции.
     * Таким образом мы получаем не только "type-safe alternative to HQL", но и безопасную и контролируем работу с результатом нашего запроса.
     * Если вы работали с QueryDsl, то эта концепция очень похожа на работу с Tuple <a href="http://querydsl.com/static/querydsl/3.4.2/reference/html/ch03s02.html">в этой библиотеки</a>
     */
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
            assertEquals(POST1_AUTHOR_NAME, post.author().username());
        }
    }

    /**
     * В данном случае мы возвращаем множество элементов (атрибутов) и памип их на DTO,
     * которое указываем при создание query jakarta.persistence.criteria.CriteriaBuilder#createQuery(java.lang.Class)
     */
    @Test
    void criteriaDto() {
        var cb = em.getCriteriaBuilder();
        var query = cb.createQuery(PostWithAuthorFlatDto.class);

        var owner = query.from(Post.class);

        var idPath = owner.<Long>get(Post_.ID);
        var slugPath = owner.<String>get(Post_.SLUG);
        var titlePath = owner.<String>get(Post_.TITLE);
        var authorIdPath = owner.get(Post_.AUTHOR).<Long>get(User_.ID);
        var authorUsernamePath = owner.get(Post_.AUTHOR).<String>get(User_.USERNAME);

        query.multiselect(idPath, slugPath, titlePath, authorIdPath, authorUsernamePath)
                .where(cb.like(cb.lower(titlePath), "%tcp%"));

        var resultList = em.createQuery(query).getResultList();

        for (PostWithAuthorFlatDto post : resultList) {
            assertEquals(POST1_SLUG, post.slug());
            assertEquals(POST1_AUTHOR_NAME, post.authorUsername());
        }
    }
}
