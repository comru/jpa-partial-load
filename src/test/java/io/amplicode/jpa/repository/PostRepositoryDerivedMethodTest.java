package io.amplicode.jpa.repository;

import io.amplicode.jpa.model.Post;
import io.amplicode.jpa.model.User;
import io.amplicode.jpa.projection.*;
import org.junit.jupiter.api.Test;
import org.springframework.aop.framework.Advised;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.List;

import static io.amplicode.jpa.InitTestDataService.POST1_SLUG;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("postgres")
class PostRepositoryDerivedMethodTest {

    @Autowired
    private PostRepository postRepository;

    //Полная загрузка всех сущностей Article, со всеми ассоциациями
    @Test
    void fullLoad() {
        List<Post> all = postRepository.findAllWithAssociations();
        for (Post post : all) {
            assertNotNull(post.getTitle());
            assertNotNull(post.getSlug());
            assertNotNull(post.getBody());
            assertNotNull(post.getCreatedAt());
            assertNotNull(post.getUpdatedAt());

            User author = post.getAuthor();
            assertNotNull(author);
            assertNotNull(author.getId());

            assertEquals(2, post.getLikeUsers().size());
        }
    }

    /**
     * Загрузка базовых атрибутов. Самый простой пример. Работает, ни каких проблем нет.
     * Из интересного, здесь стоит отметить что ArticleBasic в данном примере будет являться прокси над классом
     * org.springframework.data.jpa.repository.query.AbstractJpaQuery.TupleConverter.TupleBackedMap. TupleBackedMap это Map<Strin, Object>
     * в основе которой лежит jakarta.persistence.Tuple
     */
    @Test
    void interfaceBasic() throws Exception {
        List<PostBasic> articles = postRepository.findAllByTitleContainsIgnoreCase("tcp", PostBasic.class);
        assertEquals(1, articles.size());
        var articleFirst = articles.get(0);
        Object targetObject = getTargetObject(articleFirst);
        assertEquals("org.springframework.data.jpa.repository.query.AbstractJpaQuery$TupleConverter$TupleBackedMap", targetObject.getClass().getName());
        assertEquals(POST1_SLUG, articleFirst.getSlug());
    }

    //Также ни каких проблем, в запросе присутствуют только те колонки, которые указанны в Projection.
    // Также в этом случе проекция это прокси во круг TupleBackedMap
    @Test
    void interfaceToOneFlat() {
        List<PostWithAuthorFlat> articles = postRepository.findAllByTitleContainsIgnoreCase("tcp", PostWithAuthorFlat.class);
        assertEquals(1, articles.size());
        PostWithAuthorFlat articleFirst = articles.get(0);
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
    void interfaceToOneNested() throws Exception {
        List<PostWithAuthorNested> articles = postRepository.findAllByTitleContainsIgnoreCase("tcp", PostWithAuthorNested.class);
        assertEquals(1, articles.size());
        PostWithAuthorNested articleFirst = articles.get(0);
        Object targetObject = getTargetObject(articleFirst);
        assertEquals("org.springframework.data.jpa.repository.query.AbstractJpaQuery$TupleConverter$TupleBackedMap", targetObject.getClass().getName());
        assertEquals(POST1_SLUG, articleFirst.getSlug());
        UserPresentation author = articleFirst.getAuthor();
        assertInstanceOf(User.class, getTargetObject(author));
        assertEquals("Maksim Esteban", author.getUsername());
    }

    /**
     * Хорошая новость, это работает. Тест проходит. Плохая, что в запросе будут указаны все поля,
     * при чем как и для атрибутов первого уровня, так и для вложенных из ToMany ассоциации.
     * Ожидаемо, и объект первого уровня и объект второго являются прокси во круг сущности.
     */
    @Test
    void interfaceProjectionToManyNested() throws Exception {
        List<PostWithLikeUsersNested> articles = postRepository.findAllWithLikeUsersByTitleContainsIgnoreCase("tcp", PostWithLikeUsersNested.class);
        assertEquals(1, articles.size());
        var article = articles.get(0);
        assertInstanceOf(Post.class, getTargetObject(article));
        assertEquals(POST1_SLUG, article.getSlug());
        List<UserPresentation> likeUsers = article.getLikeUsers();
        assertEquals(2, likeUsers.size());
        UserPresentation firstLikeUser = likeUsers.get(0);
        assertInstanceOf(User.class, getTargetObject(firstLikeUser));
        assertEquals("Maksim Esteban", firstLikeUser.getUsername());
        assertEquals("Ping Sokołowski", likeUsers.get(1).getUsername());
    }

    /**
     * Работает, грузится только то что надо. Ни каких прокси, DTO будет созданно через конструктор средствами самого hibernate,
     * где-то тут org.hibernate.sql.results.internal.StandardRowReader#readRow(org.hibernate.sql.results.jdbc.spi.RowProcessingState, org.hibernate.sql.results.jdbc.spi.JdbcValuesSourceProcessingOptions)
     */
    @Test
    void classBasic() {
        List<PostBasicDto> articles = postRepository.findAllByTitleContainsIgnoreCase("tcp", PostBasicDto.class);
        assertEquals(1, articles.size());
        assertEquals(POST1_SLUG, articles.get(0).slug());
    }

    /**
     * Работает, грузится только то что надо.
     */
    @Test
    void classBasicToOneFlat() {
        var articles = postRepository.findAllByTitleContainsIgnoreCase("tcp", PostWithAuthorFlatDto.class);
        assertEquals(1, articles.size());
        var articleFirst = articles.get(0);
        assertEquals(POST1_SLUG, articleFirst.slug());
        assertEquals("Maksim Esteban", articleFirst.authorUsername());
    }

    /**
     * Это не возможно, явно получаем ошибку `Cannot set field 'author' to instantiate 'io.amplicode.jpa.projection.ArticleWithAuthorNestedDto`
     * о чем явно сказано в документации - https://docs.spring.io/spring-data/jpa/reference/repositories/projections.html#projections.dtos
     * These DTO types can be used in exactly the same way projection interfaces are used, except that no proxying happens and no nested projections can be applied.
     *
     * На самом деле, вся эта логика лежит на стороне Hibernate. Т.е. Spring Data тут ни при чем
     */
    @Test
    void classToOneNested() {
        List<PostWithAuthorNestedDto> articles = postRepository.findAllByTitleContainsIgnoreCase("tcp", PostWithAuthorNestedDto.class);
        assertEquals(1, articles.size());
        var articleFirst = articles.get(0);
        assertEquals(POST1_SLUG, articleFirst.slug());
        assertEquals("Maksim Esteban", articleFirst.author().username());
    }

    /**
     * А вот так уже будет работать, но будет проставлена вся сущность целиком и будут выгружены все поля вложеного объекта
     */
    @Test
    void classToOneNestedWithEntity() {
        List<PostWithAuthorEntity> articles = postRepository.findAllByTitleContainsIgnoreCase("tcp", PostWithAuthorEntity.class);
        assertEquals(1, articles.size());
        var articleFirst = articles.get(0);
        assertEquals(POST1_SLUG, articleFirst.slug());
        assertEquals("Maksim Esteban", articleFirst.author().getUsername());
    }


    /**
     * Не работает, почему-то не может проставить коллекцию. Надо найти тикет.
     * Но можно загрузить если likeUsers это будет не коллекция, а инстанс, но будет загруженно n * m записей
     */
    @Test
    void classToOneNestedToMany() {
        List<PostWithFavoritedDto> articles = postRepository.findAllByTitleContainsIgnoreCase("tcp", PostWithFavoritedDto.class);
        assertEquals(1, articles.size());
        var articleFirst = articles.get(0);
        assertEquals(POST1_SLUG, articleFirst.slug());
//        List<User> likeUsers = articleFirst.likeUsers().stream().toList();
//        assertEquals(2, likeUsers.size());
//        assertEquals("Maksim Esteban", likeUsers.get(0).getUsername());
//        assertEquals("Ping Sokołowski", likeUsers.get(1).getUsername());
    }

    protected Object getTargetObject(Object proxy) throws Exception {
        if (!Proxy.isProxyClass(proxy.getClass())) {
            return proxy;
        }
        InvocationHandler invocationHandler = Proxy.getInvocationHandler(proxy);
        Field advisedField = invocationHandler.getClass().getDeclaredField("advised");
        advisedField.setAccessible(true);
        Object advisedValue = advisedField.get(invocationHandler);
        if (advisedValue instanceof Advised) {
            return ((Advised) advisedValue).getTargetSource().getTarget();
        }
        return proxy;
    }
}