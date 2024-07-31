package io.amplicode.jpa.repository;

import io.amplicode.jpa.TestUtils;
import io.amplicode.jpa.model.Post;
import io.amplicode.jpa.model.Post_;
import io.amplicode.jpa.projection.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;

import static io.amplicode.jpa.InitTestDataService.POST1_SLUG;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("postgres")
class BasicAttributesTest {

    @Autowired
    private PostRepository postRepository;

    @PersistenceContext
    private EntityManager em;

    /**
     * Загрузка базовых атрибутов. Самый простой пример. Работает, ни каких проблем нет.
     * Из интересного, здесь стоит отметить что postBasic в данном примере будет являться прокси над классом
     * org.springframework.data.jpa.repository.query.AbstractJpaQuery.TupleConverter.TupleBackedMap. TupleBackedMap это Map<Strin, Object>
     * в основе которой лежит jakarta.persistence.Tuple
     */
    @Test
    void derivedMethodInterfacePrj() throws Exception {
        List<PostBasic> posts = postRepository.findAllByTitleContainsIgnoreCase("spring", PostBasic.class);
        assertEquals(1, posts.size());
        var postFirst = posts.getFirst();
        Object targetObject = TestUtils.getProxyTargetObject(postFirst);
        assertEquals(
                "org.springframework.data.jpa.repository.query.AbstractJpaQuery$TupleConverter$TupleBackedMap",
                targetObject.getClass().getName()
        );
        assertEquals(POST1_SLUG, postFirst.getSlug());
    }

    /**
     * Работает, грузится только то что надо. Ни каких прокси, DTO будет создано через конструктор средствами самого hibernate,
     * где-то тут org.hibernate.sql.results.internal.StandardRowReader#readRow(org.hibernate.sql.results.jdbc.spi.RowProcessingState, org.hibernate.sql.results.jdbc.spi.JdbcValuesSourceProcessingOptions)
     * В этом методе используется RowTransformer, который определяется тут
     * org.hibernate.query.sqm.internal.ConcreteSqmSelectQueryPlan#determineRowTransformer(org.hibernate.query.sqm.tree.select.SqmSelectStatement, java.lang.Class, org.hibernate.sql.results.internal.TupleMetadata, org.hibernate.query.spi.QueryOptions)
     */
    @Test
    void derivedMethodClassPrj() {
        List<PostBasicDto> posts = postRepository.findAllByTitleContainsIgnoreCase("spring", PostBasicDto.class);
        assertEquals(1, posts.size());
        assertEquals(POST1_SLUG, posts.getFirst().slug());
    }

    /**
     * 1. В запросе надо обязательно писать алиасы для возвращаемых атрибутов в секции select, иначе чуда не произойдет
     * и в Tuple не окажется нужных нам полей доступных по константе.
     * 2. Как создается TupleBackedMap для query запроса, ведь это не hibernate функциональность? Создается он очень просто,
     * через spring converter org.springframework.data.jpa.repository.query.AbstractJpaQuery.TupleConverter.
     */
    @Test
    void queryMethodInterfacePrj() throws Exception {
        List<PostBasic> posts = postRepository.findAllPostBase("spring");
        assertEquals(1, posts.size());
        PostBasic postFirst = posts.getFirst();
        Object targetObject = TestUtils.getProxyTargetObject(postFirst);
        assertEquals(
                "org.springframework.data.jpa.repository.query.AbstractJpaQuery$TupleConverter$TupleBackedMap",
                targetObject.getClass().getName()
        );
        assertEquals(POST1_SLUG, postFirst.getSlug());
    }

    /**
     * Наверное самый известный способ частичных загрузок, это именно через конструкцию select new.
     * Никакой магии, что написали то и получили.
     */
    @Test
    void queryMethodClassPrj() {
        var posts = postRepository.findAllPostBasicDto("spring");
        assertEquals(1, posts.size());
        PostBasicDto postBasic = posts.getFirst();
        assertEquals(POST1_SLUG, postBasic.slug());
    }

    @Test
    void queryMethodObjectArray() {
        var posts = postRepository.findAllObjectArrayBasic("spring");
        assertEquals(1, posts.size());
        var post = posts.getFirst();
        assertEquals(POST1_SLUG, post[1]);
    }

    /**
     * Все также в методе org.hibernate.query.sqm.internal.ConcreteSqmSelectQueryPlan#determineRowTransformer(org.hibernate.query.sqm.tree.select.SqmSelectStatement, java.lang.Class, org.hibernate.sql.results.internal.TupleMetadata, org.hibernate.query.spi.QueryOptions)
     * находится подходящий RowTransformer, в нашем случае это будет RowTransformerJpaTupleImpl и также если не написать алиасы,
     * то из тупла нельзя будет вытащить значение по имени, только по порядку.
     * Также обратим внимание, что мы используем константы из класса Post_, они автогенерятся за счет библиотеки org.hibernate:hibernate-jpamodelgen
     * В последней документации hibernate данный способ используется во всех примерах, можно сказать, что это тихая рекомендация.
     */
    @Test
    void queryMethodTuple() {
        var posts = postRepository.findAllTupleBasic("spring");
        assertEquals(1, posts.size());
        Tuple post = posts.getFirst();
        assertEquals(POST1_SLUG, post.get(Post_.SLUG));
    }

    @Test
    void queryMethodMap() {
        var posts = postRepository.findAllMapBasic("spring");
        assertEquals(1, posts.size());
        Map<String, Object> post = posts.getFirst();
        assertEquals(POST1_SLUG, post.get(Post_.SLUG));
    }

    @Test
    void queryMethodList() {
        var posts = postRepository.findAllListBasic("spring");
        assertEquals(1, posts.size());
        List<Object> post = posts.getFirst();
        assertEquals(POST1_SLUG, post.get(1));
    }

    @Test
    void emObjectArray() {
        var posts = em.createQuery("""
                        select a.id as id, a.slug as slug, a.title as title from Post a
                        where lower(a.title) like lower(concat('%', ?1, '%'))""", Object[].class)
                .setParameter(1, "spring")
                .getResultList();
        assertEquals(1, posts.size());
        var post = posts.getFirst();
        assertEquals(POST1_SLUG, post[1]);
    }

    @Test
    void emTuple() {
        var posts = em.createQuery("""
                        select a.id as id, a.slug as slug, a.title as title from Post a
                        where lower(a.title) like lower(concat('%', ?1, '%'))""", Tuple.class)
                .setParameter(1, "spring")
                .getResultList();
        assertEquals(1, posts.size());
        var post = posts.getFirst();
        assertEquals(POST1_SLUG, post.get(Post_.SLUG));
    }

    @Test
    void emClassWithoutConstructor() {
        var posts = em.createQuery("""
                        select a.id, a.slug, a.title from Post a
                        where lower(a.title) like lower(concat('%', ?1, '%'))""", PostBasicDto.class)
                .setParameter(1, "spring")
                .getResultList();
        assertEquals(1, posts.size());
        var post = posts.getFirst();
        assertEquals(POST1_SLUG, post.slug());
    }

    @Test
    void emClassWithConstructor() {
        var posts = em.createQuery("""
                        select new io.amplicode.jpa.projection.PostBasicDto(a.id, a.slug, a.title) from Post a
                        where lower(a.title) like lower(concat('%', ?1, '%'))""", PostBasicDto.class)
                .setParameter(1, "spring")
                .getResultList();
        assertEquals(1, posts.size());
        var post = posts.getFirst();
        assertEquals(POST1_SLUG, post.slug());
    }

    @Test
    void emMap() {
        var posts = em.createQuery("""
                        select a.id as id, a.slug as slug, a.title as title from Post a
                        where lower(a.title) like lower(concat('%', ?1, '%'))""", Map.class)
                .setParameter(1, "spring")
                .getResultList();
        assertEquals(1, posts.size());
        var post = posts.getFirst();
        assertEquals(POST1_SLUG, post.get(Post_.SLUG));
    }

    @Test
    void emList() {
        var posts = em.createQuery("""
                        select a.id as id, a.slug as slug, a.title as title from Post a
                        where lower(a.title) like lower(concat('%', ?1, '%'))""", List.class)
                .setParameter(1, "spring")
                .getResultList();
        assertEquals(1, posts.size());
        var post = posts.getFirst();
        assertEquals(POST1_SLUG, post.get(1));
    }

    @Test
    void criteriaObjectArray() {
        var cb = em.getCriteriaBuilder();
        var query = cb.createQuery(Object[].class);

        var owner = query.from(Post.class);
        var idPath = owner.get(Post_.ID);
        var slugPath = owner.get(Post_.SLUG);
        var titlePath = owner.<String>get(Post_.TITLE);
        query.multiselect(idPath, slugPath, titlePath)
                .where(cb.like(cb.lower(titlePath), "%tcp%"));

        var resultList = em.createQuery(query).getResultList();
        for (Object[] ownerDto : resultList) {
            assertEquals(POST1_SLUG, ownerDto[1]);
        }
    }

    @Test
    void criteriaTuple() {
        var cb = em.getCriteriaBuilder();
        var query = cb.createTupleQuery();

        var owner = query.from(Post.class);
        var idPath = owner.get(Post_.ID);
        var slugPath = owner.get(Post_.SLUG);
        var titlePath = owner.<String>get(Post_.TITLE);
        query.select(cb.tuple(idPath, slugPath, titlePath))
                .where(cb.like(cb.lower(titlePath), "%tcp%"));

        var resultList = em.createQuery(query).getResultList();
        for (Tuple post : resultList) {
            assertEquals(POST1_SLUG, post.get(slugPath));
        }
    }

    @Test
    void criteriaClass() {
        var cb = em.getCriteriaBuilder();
        var query = cb.createQuery(PostBasicDto.class);

        var owner = query.from(Post.class);
        var idPath = owner.get(Post_.ID);
        var slugPath = owner.get(Post_.SLUG);
        var titlePath = owner.<String>get(Post_.TITLE);
        query.multiselect(idPath, slugPath, titlePath)
                .where(cb.like(cb.lower(titlePath), "%tcp%"));

        var resultList = em.createQuery(query).getResultList();
        for (PostBasicDto post : resultList) {
            assertEquals(POST1_SLUG, post.slug());
        }
    }

    @Test
    void criteriaList() {
        var cb = em.getCriteriaBuilder();
        var query = cb.createQuery(List.class);

        var owner = query.from(Post.class);
        var idPath = owner.get(Post_.ID);
        var slugPath = owner.get(Post_.SLUG);
        var titlePath = owner.<String>get(Post_.TITLE);
        query.multiselect(idPath, slugPath, titlePath)
                .where(cb.like(cb.lower(titlePath), "%tcp%"));

        var resultList = em.createQuery(query).getResultList();
        for (List<?> ownerDto : resultList) {
            assertEquals(POST1_SLUG, ownerDto.get(1));
        }
    }
}