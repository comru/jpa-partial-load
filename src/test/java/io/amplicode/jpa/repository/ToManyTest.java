package io.amplicode.jpa.repository;

import io.amplicode.jpa.model.Post;
import io.amplicode.jpa.model.Post_;
import io.amplicode.jpa.model.User_;
import io.amplicode.jpa.projection.PostWithLikeUsersDto;
import io.amplicode.jpa.projection.PostWithLikeUsersFlat;
import io.amplicode.jpa.projection.UserPresentationDto;
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

import static io.amplicode.jpa.InitTestDataService.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("postgres")
public class ToManyTest {

    @Autowired
    private PostRepository postRepository;

    @PersistenceContext
    private EntityManager em;

    /**
     * Работает это как-то так. Сам hibernate не умеет нормально мапить коллекционные атрибуты в DTO/Projection.
     * HQL вернет нам n + m, где n - это количество записей post по нашему запросу, а m это сумма всех связанных с этими постами like users.
     * Условно, запрос будет такого вида
     * <code>
     *     Hibernate:
     *     select
     *         p1_0.id,
     *         p1_0.slug,
     *         p1_0.title,
     *         lu1_0.user_id,
     *         lu1_1.username
     *     from
     *         posts p1_0
     *     left join
     *         post_like_users lu1_0
     *             on p1_0.id=lu1_0.post_id
     *     left join
     *         users lu1_1
     *             on lu1_1.id=lu1_0.user_id
     *     where
     *         upper(p1_0.title) like upper(?) escape '\'
     * </code>
     * <p>
     * На наших тестовых данных есть всего один post, который удовлетворяет этому запросу, но записей вернеться две,
     * т.к. у этого поста два like users, и произойдет некоторый дубляж данных для p1_0.id, p1_0.slug, p1_0.title
     */
    @Test
    void derivedMethodInterfaceFlatPrj() throws Exception {
        List<PostWithLikeUsersFlat> posts = postRepository.findAllByTitleContainsIgnoreCase("spring", PostWithLikeUsersFlat.class);
        assertEquals(2, posts.size());
        var post = posts.getFirst();
        assertEquals(POST1_SLUG, post.getSlug());

        PostWithLikeUsersFlat post1 = posts.get(0);
        PostWithLikeUsersFlat post2 = posts.get(1);
        assertEquals(post1.getId(), post2.getId());
        assertEquals(POST1_AUTHOR_NAME, post1.getLikeUsersUsername());
        assertEquals(POST2_AUTHOR_NAME, post2.getLikeUsersUsername());
    }


    /**
     * Такой же side effect как и в кейсе с проекцией, загружается n + m записей.
     */
    @Test
    void derivedMethodClassFlat() {
        List<PostWithLikeUsersDto> post = postRepository.findAllByTitleContainsIgnoreCase("spring", PostWithLikeUsersDto.class);
        assertEquals(2, post.size());
        var postFirst = post.getFirst();
        assertEquals(POST1_SLUG, postFirst.slug());

        var post1 = post.get(0);
        var post2 = post.get(1);
        assertEquals(post1.id(), post2.id());
        assertEquals(POST1_AUTHOR_NAME, post1.likeUsersUsername());
        assertEquals(POST2_AUTHOR_NAME, post2.likeUsersUsername());
    }

    @Test
    void queryMethodInterfaceFlatPrj() {
        var posts = postRepository.findInterfaceFlatPrj("spring");
        assertEquals(2, posts.size());
        var post = posts.getFirst();
        assertEquals(POST1_SLUG, post.getSlug());

        var post1 = posts.get(0);
        var post2 = posts.get(1);
        assertEquals(post1.getId(), post2.getId());
        assertEquals(POST1_AUTHOR_NAME, post1.getLikeUsersUsername());
        assertEquals(POST2_AUTHOR_NAME, post2.getLikeUsersUsername());
    }

    @Test
    void queryMethodClassFlatPrj() {
        var posts = postRepository.findToManyClassFlat("spring");
        assertEquals(2, posts.size());
        var post = posts.getFirst();
        assertEquals(POST1_SLUG, post.slug());

        var post1 = posts.get(0);
        var post2 = posts.get(1);
        assertEquals(post1.id(), post2.id());
        assertEquals(POST1_AUTHOR_NAME, post1.likeUsersUsername());
        assertEquals(POST2_AUTHOR_NAME, post2.likeUsersUsername());
    }

    @Test
    void queryMethodClassNested() {
        var posts = postRepository.findToManyClassNested("spring");
        assertEquals(2, posts.size());
        var post = posts.getFirst();
        assertEquals(POST1_SLUG, post.slug());

        var post1 = posts.get(0);
        var post2 = posts.get(1);
        assertEquals(post1.id(), post2.id());
        assertEquals(POST1_AUTHOR_NAME, post1.likeUser().username());
        assertEquals(POST2_AUTHOR_NAME, post2.likeUser().username());
    }

    /**
     * В данном примере, мы выполняем запрос через @Query метод и получаем Tuple как результат.
     * Затем мы перекладываем выгруженные данные уже в готовый DTO класс.
     * Также решаем проблему схлопывания дубликатов
     */
    @Test
    void queryMethodTuple() {
        var postTuples = postRepository.findToManyTuple("spring");
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

        assertEquals(POST1_SLUG, firstPost.slug());
        List<UserPresentationDto> likeUsers = firstPost.likeUsers;
        assertEquals(2, likeUsers.size());
        assertEquals(POST1_AUTHOR_NAME, likeUsers.get(0).username());
        assertEquals(POST2_AUTHOR_NAME, likeUsers.get(1).username());
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
                .where(cb.like(cb.lower(titlePath), "%spring%"));

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

        assertEquals(POST1_SLUG, firstPost.slug());
        List<UserPresentationDto> likeUsers = firstPost.likeUsers;
        assertEquals(2, likeUsers.size());
        assertEquals(POST1_AUTHOR_NAME, likeUsers.get(0).username());
        assertEquals(POST2_AUTHOR_NAME, likeUsers.get(1).username());
    }

    private record PostWithLikeUsers(Long id,
                                     String slug,
                                     String title,
                                     List<UserPresentationDto> likeUsers) {

    }
}
