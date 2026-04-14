package net.chrisrichardson.ftgo.consumerservice.standalone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableAutoConfiguration
@ComponentScan
@EntityScan(basePackages = "net.chrisrichardson.ftgo.domain")
@EnableJpaRepositories(basePackages = "net.chrisrichardson.ftgo.domain")
@Import(ConsumerServiceStandaloneConfiguration.class)
public class ConsumerServiceStandaloneApplication {

  public static void main(String[] args) {
    SpringApplication.run(ConsumerServiceStandaloneApplication.class, args);
  }
}
