package net.chrisrichardson.ftgo.consumerservice.microservice;

import net.chrisrichardson.ftgo.common.CommonConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "net.chrisrichardson.ftgo.consumerservice.microservice")
@EnableJpaRepositories(basePackages = "net.chrisrichardson.ftgo.consumerservice.microservice")
@Import(CommonConfiguration.class)
public class ConsumerServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(ConsumerServiceApplication.class, args);
  }
}
