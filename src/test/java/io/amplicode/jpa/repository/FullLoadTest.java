package io.amplicode.jpa.repository;

import io.amplicode.jpa.model.Post;
import io.amplicode.jpa.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("postgres")
public class FullLoadTest {

    @Autowired
    private PostRepository postRepository;

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
}
