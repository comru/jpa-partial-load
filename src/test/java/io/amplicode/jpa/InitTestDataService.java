package io.amplicode.jpa;

import io.amplicode.jpa.model.Article;
import io.amplicode.jpa.model.Owner;
import io.amplicode.jpa.model.Tag;
import io.amplicode.jpa.model.User;
import io.amplicode.jpa.repository.ArticleRepository;
import io.amplicode.jpa.repository.OwnerRepository;
import io.amplicode.jpa.repository.TagRepository;
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

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TagRepository tagRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void doSomethingAfterStartup() {
        Owner owner = new Owner();
        owner.setFirstName("George");
        owner.setLastName("Franklin");
        owner.setAddress("110 W. Liberty St.");
        owner.setCity("Madison");
        owner.setTelephone("6085551023");
        ownerRepository.save(owner);

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

        Set<Tag> tags = Set.of(
                new Tag("sit"), new Tag("reiciendis"), new Tag("consequuntur"), new Tag("nihil")
        );
        tagRepository.saveAll(tags);

        Article article = new Article();
        article.setTitle("Ill quantify the redundant TCP bus, that should hard drive the ADP bandwidth!");
        article.setBody("Quis nesciunt ut est eos.\nQui reiciendis doloribus.\nEst quidem ullam reprehenderit.\nEst omnis eligendi quis quis quo eum officiis asperiores quis. Et sed dicta eveniet accusamus consequatur.\nUllam voluptas consequatur aut eos ducimus.\nId officia est ut dicta provident beatae ipsa. Pariatur quo neque est perspiciatis non illo rerum expedita minima.\nEt commodi voluptas eos ex.\nUnde velit delectus deleniti deleniti non in sit.\nAliquid voluptatem magni. Iusto laborum aperiam neque delectus consequuntur provident est maiores explicabo. Est est sed itaque necessitatibus vitae officiis.\nIusto dolores sint eveniet quasi dolore quo laborum esse laboriosam.\nModi similique aut voluptates animi aut dicta dolorum.\nSint explicabo autem quidem et.\nNeque aspernatur assumenda fugit provident. Et fuga repellendus magnam dignissimos eius aspernatur rerum. Dolorum eius dignissimos et magnam voluptate aut voluptatem natus.\nAut sint est eum molestiae consequatur officia omnis.\nQuae et quam odit voluptatum itaque ducimus magni dolores ab.\nDolorum sed iure voluptatem et reiciendis. Eveniet sit ipsa officiis laborum.\nIn vel est omnis sed impedit quod magni.\nDignissimos quis illum qui atque aut ut quasi sequi. Eveniet sit ipsa officiis laborum.\nIn vel est omnis sed impedit quod magni.\nDignissimos quis illum qui atque aut ut quasi sequi. Sapiente vitae culpa ut voluptatem incidunt excepturi voluptates exercitationem.\nSed doloribus alias consectetur omnis occaecati ad placeat labore.\nVoluptate consequatur expedita nemo recusandae sint assumenda.\nQui vel totam quia fugit saepe suscipit autem quasi qui.\nEt eum vel ut delectus ut nesciunt animi.");
        article.setSlug("Ill-quantify-the-redundant-TCP-bus-that-should-hard-drive-the-ADP-bandwidth!-553");
        article.setCreatedAt(Instant.now());
        article.setUpdatedAt(Instant.now());
        article.setAuthor(esteban);
        article.setFavorited(Set.of(esteban, sokolowski));
        article.setTags(tags);

        articleRepository.save(article);
    }

    @PreDestroy
    public void destroy() {
        ownerRepository.deleteAll();

        articleRepository.deleteAll();
        userRepository.deleteAll();
        tagRepository.deleteAll();
    }
}
