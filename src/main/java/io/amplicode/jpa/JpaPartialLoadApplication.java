package io.amplicode.jpa;

import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

@SpringBootApplication
@EnableConfigurationProperties
public class JpaPartialLoadApplication {

	public static void main(String[] args) {
		SpringApplication.run(JpaPartialLoadApplication.class, args);
	}

//	@Bean
//	public StatelessSession statelessSession(LocalContainerEntityManagerFactoryBean entityManagerFactoryBean) {
//		return entityManagerFactoryBean.getObject().unwrap(SessionFactory.class).openStatelessSession();
//	}
//
//	@Bean
//	public JakartaOwnerRepository postRepository(StatelessSession statelessSession) {
//		return new JakartaOwnerRepository_(statelessSession);
//	}
}
