package net.chrisrichardson.ftgo.restaurantservice.standalone;

import net.chrisrichardson.ftgo.restaurantservice.standalone.domain.RestaurantService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestaurantServiceStandaloneConfiguration {

  @Bean
  public RestaurantService restaurantService() {
    return new RestaurantService();
  }
}
