package io.amplicode.jpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class JpaPartialLoadApplication {

	public static void main(String[] args) {
		SpringApplication.run(JpaPartialLoadApplication.class, args);
	}
}
