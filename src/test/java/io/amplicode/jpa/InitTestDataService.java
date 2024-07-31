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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class InitTestDataService {

    public static final String POST1_SLUG = "faster-startup-with-spring-boot-32-and-crac-part-1";
    public static final String POST1_AUTHOR_NAME = "Ivan Ivanov";
    public static final String POST2_AUTHOR_NAME = "Petr Petrov";

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void initTestData() {
        User ivan = new User();
        ivan.setUsername(POST1_AUTHOR_NAME);
        ivan.setBio("Bio for Ivanov");
        ivan.setEmail("ivanov@mail.com");
        ivan.setPassword("####");

        User petr = new User();
        petr.setUsername(POST2_AUTHOR_NAME);
        petr.setBio("Bio for Petrov");
        petr.setEmail("petrov@mail.com");
        petr.setPassword("####");

        userRepository.saveAll(List.of(ivan, petr));

        Post post1 = new Post();
        post1.setTitle("Faster Startup With Spring Boot 3.2 and CRaC, Part 1: Automatic Checkpoint");
        post1.setBody("With Spring Boot 3.2 and Spring Framework 6.1, we get support for Coordinated Restore at Checkpoint (CRaC), a mechanism that enables Java applications to start up faster. With Spring Boot, we can use CRaC in a simplified way, known as Automatic Checkpoint/Restore at startup. Even though not as powerful as the standard way of using CRaC, this blog post will show an example where the Spring Boot applications startup time is decreased by 90%. The sample applications are from chapter 6 in my book on building microservices with Spring Boot.");
        post1.setSlug(POST1_SLUG);
        post1.setCreatedAt(Instant.now());
        post1.setUpdatedAt(Instant.now());
        post1.setAuthor(ivan);
        post1.setLikeUsers(new LinkedHashSet<>(List.of(ivan, petr)));

        Post post2 = new Post();
        post2.setTitle("Virtual Threads: A Game-Changer for Concurrency");
        post2.setBody("Despite being nearly 30 years old, the Java platform remains consistently among the top three most popular programming languages. This enduring popularity can be attributed to the Java Virtual Machine (JVM), which abstracts complexities such as memory management and compiles code during execution, enabling unparalleled internet-level scalability.\n" +
                "\n" +
                "Java's sustained relevance is also due to the rapid evolution of the language, its libraries, and the JVM. Java Virtual Threads, introduced in Project Loom, which is an initiative by the OpenJDK community, represent a groundbreaking change in how Java handles concurrency. ");
        post2.setSlug("deep-dive-into-java-virtual-threads-a-game-changer");
        post2.setCreatedAt(Instant.now());
        post2.setUpdatedAt(Instant.now());
        post2.setAuthor(petr);
        post2.setLikeUsers(Set.of(ivan, petr));

        postRepository.saveAll(List.of(post1, post2));
    }

    @PreDestroy
    public void destroy() {
        postRepository.deleteAll();
        userRepository.deleteAll();
    }
}
