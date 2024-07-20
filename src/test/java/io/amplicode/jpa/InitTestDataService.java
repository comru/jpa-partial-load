package io.amplicode.jpa;

import io.amplicode.jpa.model.Post;
import io.amplicode.jpa.model.User;
import io.amplicode.jpa.repository.PostRepository;
import io.amplicode.jpa.repository.UserRepository;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@Service
public class InitTestDataService {

    public static final String POST1_SLUG = "Ill-quantify-the-redundant-TCP-bus-that-should-hard-drive-the-ADP-bandwidth!-553";

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void initTestData() {
        User esteban = new User();
        esteban.setUsername("Maksim Esteban");
        esteban.setBio("Bio");
        esteban.setEmail("esteban@mail.com");
        esteban.setPassword("####");

        User sokolowski = new User();
        sokolowski.setUsername("Ping Sokołowski");
        sokolowski.setBio("Bio");
        sokolowski.setEmail("sokolowski@mail.com");
        sokolowski.setPassword("####");

        userRepository.saveAll(List.of(esteban, sokolowski));

        Post post1 = new Post();
        post1.setTitle("Ill quantify the redundant TCP bus, that should hard drive the ADP bandwidth!");
        post1.setBody("Quis nesciunt ut est eos.\nQui reiciendis doloribus.\nEst quidem ullam reprehenderit.\nEst omnis eligendi quis quis quo eum officiis asperiores quis. Et sed dicta eveniet accusamus consequatur.\nUllam voluptas consequatur aut eos ducimus.\nId officia est ut dicta provident beatae ipsa. Pariatur quo neque est perspiciatis non illo rerum expedita minima.\nEt commodi voluptas eos ex.\nUnde velit delectus deleniti deleniti non in sit.\nAliquid voluptatem magni. Iusto laborum aperiam neque delectus consequuntur provident est maiores explicabo. Est est sed itaque necessitatibus vitae officiis.\nIusto dolores sint eveniet quasi dolore quo laborum esse laboriosam.\nModi similique aut voluptates animi aut dicta dolorum.\nSint explicabo autem quidem et.\nNeque aspernatur assumenda fugit provident. Et fuga repellendus magnam dignissimos eius aspernatur rerum. Dolorum eius dignissimos et magnam voluptate aut voluptatem natus.\nAut sint est eum molestiae consequatur officia omnis.\nQuae et quam odit voluptatum itaque ducimus magni dolores ab.\nDolorum sed iure voluptatem et reiciendis. Eveniet sit ipsa officiis laborum.\nIn vel est omnis sed impedit quod magni.\nDignissimos quis illum qui atque aut ut quasi sequi. Eveniet sit ipsa officiis laborum.\nIn vel est omnis sed impedit quod magni.\nDignissimos quis illum qui atque aut ut quasi sequi. Sapiente vitae culpa ut voluptatem incidunt excepturi voluptates exercitationem.\nSed doloribus alias consectetur omnis occaecati ad placeat labore.\nVoluptate consequatur expedita nemo recusandae sint assumenda.\nQui vel totam quia fugit saepe suscipit autem quasi qui.\nEt eum vel ut delectus ut nesciunt animi.");
        post1.setSlug("Ill-quantify-the-redundant-TCP-bus-that-should-hard-drive-the-ADP-bandwidth!-553");
        post1.setCreatedAt(Instant.now());
        post1.setUpdatedAt(Instant.now());
        post1.setAuthor(esteban);
        post1.setLikeUsers(Set.of(esteban, sokolowski));

        Post post2 = new Post();
        post2.setTitle("quantifying the circuit wont do anything, we need to parse the back-end FTP interface!");
        post2.setBody("Quos pariatur tenetur.\nQuasi omnis eveniet eos maiores esse magni possimus blanditiis.\nQui incidunt sit quos consequatur aut qui et aperiam delectus.\nPraesentium quas culpa.\nEaque occaecati cumque incidunt et. Laborum est maxime enim accusantium magnam.\nRerum dolorum minus laudantium delectus eligendi necessitatibus quia.\nDeleniti consequatur explicabo aut nobis est vero tempore.\nExcepturi earum quo quod voluptatem quo iure vel sapiente occaecati.\nConsectetur consequatur corporis doloribus omnis harum voluptas esse amet. Quia quo iste et aperiam voluptas consectetur a omnis et.\nDolores et earum consequuntur sunt et.\nEa nulla ab voluptatem dicta vel. Officia consectetur quibusdam velit debitis porro quia cumque.\nSuscipit esse voluptatem cum sit totam consequatur molestiae est.\nMollitia pariatur distinctio fugit. Officia consectetur quibusdam velit debitis porro quia cumque.\nSuscipit esse voluptatem cum sit totam consequatur molestiae est.\nMollitia pariatur distinctio fugit. Ab rerum eos ipsa accusantium nihil voluptatem.\nEum minus alias.\nIure commodi at harum.\nNostrum non occaecati omnis quisquam. Sapiente maxime sequi. Quia quo iste et aperiam voluptas consectetur a omnis et.\nDolores et earum consequuntur sunt et.\nEa nulla ab voluptatem dicta vel. Similique et quos maiores commodi exercitationem laborum animi qui. Consequatur exercitationem asperiores quidem fuga rerum voluptas pariatur.\nRepellendus sit itaque nam.\nDeleniti consectetur vel aliquam vitae est velit.\nId blanditiis ullam sed consequatur omnis.\n");
        post2.setSlug("quantifying-the-circuit-wont-do-anything-we-need-to-parse-the-back-end-FTP-interface!-553");
        post2.setCreatedAt(Instant.now());
        post2.setUpdatedAt(Instant.now());
        post2.setAuthor(sokolowski);
        post2.setLikeUsers(Set.of(esteban, sokolowski));

        postRepository.saveAll(List.of(post1, post2));
    }

    @PreDestroy
    public void destroy() {
        postRepository.deleteAll();
        userRepository.deleteAll();
    }
}
