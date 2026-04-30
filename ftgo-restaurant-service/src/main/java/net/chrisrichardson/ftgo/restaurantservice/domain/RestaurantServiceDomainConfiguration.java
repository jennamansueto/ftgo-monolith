package net.chrisrichardson.ftgo.restaurantservice.domain;

import net.chrisrichardson.ftgo.common.CommonConfiguration;
import net.chrisrichardson.ftgo.domain.Restaurant;
import net.chrisrichardson.ftgo.domain.RestaurantRepository;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan(basePackageClasses = Restaurant.class)
@EnableJpaRepositories(basePackageClasses = RestaurantRepository.class)
@Import(CommonConfiguration.class)
public class RestaurantServiceDomainConfiguration {

  @Bean
  public RestaurantService restaurantService() {
    return new RestaurantService();
  }
}
