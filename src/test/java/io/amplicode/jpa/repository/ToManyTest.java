package io.amplicode.jpa.repository;

import io.amplicode.jpa.TestUtils;
import io.amplicode.jpa.model.Post;
import io.amplicode.jpa.model.Post_;
import io.amplicode.jpa.model.User;
import io.amplicode.jpa.model.User_;
import io.amplicode.jpa.projection.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static io.amplicode.jpa.InitTestDataService.POST1_SLUG;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@SpringBootTest
@ActiveProfiles("postgres")
public class ToManyTest {

    @Autowired
    private PostRepository postRepository;

    @PersistenceContext
    private EntityManager em;

    /**
     * Хорошая новость, это работает. Тест проходит. Плохая, что в запросе будут указаны все поля,
     * при чем как и для атрибутов первого уровня, так и для вложенных из ToMany ассоциации.
     * Ожидаемо, и объект первого уровня и объект второго являются прокси во круг сущности.
     */
    @Test
    void derivedMethodInterfaceNestedPrj() throws Exception {
        List<PostWithLikeUsersNested> articles = postRepository.findAllWithLikeUsersByTitleContainsIgnoreCase("tcp", PostWithLikeUsersNested.class);
        assertEquals(1, articles.size());
        var article = articles.get(0);
        assertInstanceOf(Post.class, TestUtils.getProxyTargetObject(article));
        assertEquals(POST1_SLUG, article.getSlug());
        List<UserPresentation> likeUsers = article.getLikeUsers();
        assertEquals(2, likeUsers.size());
        UserPresentation firstLikeUser = likeUsers.get(0);
        assertInstanceOf(User.class, TestUtils.getProxyTargetObject(firstLikeUser));
        assertEquals("Maksim Esteban", firstLikeUser.getUsername());
        assertEquals("Ping Sokołowski", likeUsers.get(1).getUsername());
    }

    /**
     * Вообще что из себя представляет запрос при попытке загрузить to-many связь. Это произведение
     *
     * @throws Exception
     */
    @Test
    void derivedMethodInterfaceFlatPrj() throws Exception {
        List<PostWithLikeUsersAsFlat> articles = postRepository.findAllWithLikeUsersByTitleContainsIgnoreCase("tcp", PostWithLikeUsersAsFlat.class);
        assertEquals(2, articles.size());
        var article = articles.getFirst();
        assertEquals("org.springframework.data.jpa.repository.query.AbstractJpaQuery$TupleConverter$TupleBackedMap", TestUtils.getProxyTargetObject(article).getClass().getName());
        assertEquals(POST1_SLUG, article.getSlug());
//        List<UserPresentation> likeUsers = article.getLikeUsers();
//        assertEquals(2, likeUsers.size());
//        UserPresentation firstLikeUser = likeUsers.get(0);
//        assertInstanceOf(User.class, TestUtils.getProxyTargetObject(firstLikeUser));
//        assertEquals("Maksim Esteban", firstLikeUser.getUsername());
//        assertEquals("Ping Sokołowski", likeUsers.get(1).getUsername());
    }

    /**
     * C flat dct в порядке
     */
    @Test
    void derivedMethodClassFlat() {
        List<PostWithLikeUsersDto> articles = postRepository.findAllByTitleContainsIgnoreCase("tcp", PostWithLikeUsersDto.class);
        assertEquals(2, articles.size());
        var articleFirst = articles.getFirst();
        assertEquals(POST1_SLUG, articleFirst.slug());
//        List<User> likeUsers = articleFirst.likeUsers().stream().toList();
//        assertEquals(2, likeUsers.size());
//        assertEquals("Maksim Esteban", likeUsers.get(0).getUsername());
//        assertEquals("Ping Sokołowski", likeUsers.get(1).getUsername());
    }

    @Test
    void queryMethodInterfaceFlatPrj() {
        var articles = postRepository.findInterfaceFlatPrj("tcp");
        assertEquals(2, articles.size());
        var article = articles.getFirst();
        assertEquals("Ill-quantify-the-redundant-TCP-bus-that-should-hard-drive-the-ADP-bandwidth!-553", article.getSlug());
        assertEquals("Maksim Esteban", article.getLikeUsersUsername());
    }

    @Test
    void queryMethodClassFlatPrj() {
        var articles = postRepository.findToManyClassFlat("tcp");
        assertEquals(2, articles.size());
        var article = articles.getFirst();
        assertEquals("Ill-quantify-the-redundant-TCP-bus-that-should-hard-drive-the-ADP-bandwidth!-553", article.slug());
        assertEquals("Maksim Esteban", article.likeUsersUsername());
    }

    @Test
    void queryMethodClassNested() {
        var articles = postRepository.findToManyClassNested("tcp");
        assertEquals(2, articles.size());
        var article = articles.getFirst();
        assertEquals("Ill-quantify-the-redundant-TCP-bus-that-should-hard-drive-the-ADP-bandwidth!-553", article.slug());
        assertEquals("Maksim Esteban", article.likeUser().username());
    }

    @Test
    void queryMethodTuple() {
        var postTuples = postRepository.findToManyTuple("tcp");
        assertEquals(2, postTuples.size());

        var postMap = new LinkedHashMap<Long, PostWithLikeUsers>();

        for (Tuple tuple : postTuples) {
            var postId = tuple.get("id", Long.class);
            var postWithLikeUsers = postMap.computeIfAbsent(postId, id ->
                    new PostWithLikeUsers(
                            id,
                            tuple.get("slug", String.class),
                            tuple.get("title", String.class),
                            new ArrayList<>()
                    ));
            postWithLikeUsers.likeUsers.add(new UserPresentationDto(
                    tuple.get("likeUserId", Long.class),
                    tuple.get("likeUserUsername", String.class)
            ));
        }

        List<PostWithLikeUsers> posts = postMap.values().stream().toList();
        assertEquals(1, posts.size());
        PostWithLikeUsers firstPost = posts.getFirst();

        assertEquals("Ill-quantify-the-redundant-TCP-bus-that-should-hard-drive-the-ADP-bandwidth!-553", firstPost.slug());
        List<UserPresentationDto> likeUsers = firstPost.likeUsers;
        assertEquals(2, likeUsers.size());
        assertEquals("Maksim Esteban", likeUsers.get(0).username());
        assertEquals("Ping Sokołowski", likeUsers.get(1).username());
    }

    @Test
    void criteriaTuple() {
        var cb = em.getCriteriaBuilder();
        var query = cb.createTupleQuery();

        var owner = query.from(Post.class);

        var idPath = owner.<Long>get(Post_.ID);
        var slugPath = owner.<String>get(Post_.SLUG);
        var titlePath = owner.<String>get(Post_.TITLE);
        var likeUsersIdPath = owner.get(Post_.LIKE_USERS).<Long>get(User_.ID);
        var likeUsersUsernamePath = owner.get(Post_.LIKE_USERS).<String>get(User_.USERNAME);

        query.select(cb.tuple(idPath, slugPath, titlePath, likeUsersIdPath, likeUsersUsernamePath))
                .where(cb.like(cb.lower(titlePath), "%tcp%"));

        List<Tuple> postTuples = em.createQuery(query).getResultList();

        var postMap = new LinkedHashMap<Long, PostWithLikeUsers>();

        for (Tuple tuple : postTuples) {
            var postId = tuple.get(idPath);
            var postWithLikeUsers = postMap.computeIfAbsent(postId, id ->
                    new PostWithLikeUsers(
                            id,
                            tuple.get(slugPath),
                            tuple.get(titlePath),
                            new ArrayList<>()
                    ));
            postWithLikeUsers.likeUsers.add(new UserPresentationDto(
                    tuple.get(likeUsersIdPath),
                    tuple.get(likeUsersUsernamePath)
            ));
        }

        List<PostWithLikeUsers> posts = postMap.values().stream().toList();
        assertEquals(1, posts.size());
        PostWithLikeUsers firstPost = posts.getFirst();

        assertEquals("Ill-quantify-the-redundant-TCP-bus-that-should-hard-drive-the-ADP-bandwidth!-553", firstPost.slug());
        List<UserPresentationDto> likeUsers = firstPost.likeUsers;
        assertEquals(2, likeUsers.size());
        assertEquals("Maksim Esteban", likeUsers.get(0).username());
        assertEquals("Ping Sokołowski", likeUsers.get(1).username());
    }

    private record PostWithLikeUsers(Long id,
                                     String slug,
                                     String title,
                                     List<UserPresentationDto> likeUsers) {

    }
}
