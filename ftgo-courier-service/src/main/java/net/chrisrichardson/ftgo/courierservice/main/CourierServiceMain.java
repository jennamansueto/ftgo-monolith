package net.chrisrichardson.ftgo.courierservice.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = "net.chrisrichardson.ftgo.courierservice")
@EntityScan(basePackages = "net.chrisrichardson.ftgo.courierservice.persistence")
@EnableJpaRepositories(basePackages = "net.chrisrichardson.ftgo.courierservice.persistence")
public class CourierServiceMain {

  public static void main(String[] args) {
    SpringApplication.run(CourierServiceMain.class, args);
  }
}
