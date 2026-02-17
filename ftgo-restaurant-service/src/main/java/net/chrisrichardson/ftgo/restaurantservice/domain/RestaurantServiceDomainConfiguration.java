package net.chrisrichardson.ftgo.restaurantservice.domain;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan(basePackages = "net.chrisrichardson.ftgo.restaurantservice.persistence")
@EnableJpaRepositories(basePackages = "net.chrisrichardson.ftgo.restaurantservice.persistence")
public class RestaurantServiceDomainConfiguration {

  @Bean
  public RestaurantService restaurantService() {
    return new RestaurantService();
  }
}
