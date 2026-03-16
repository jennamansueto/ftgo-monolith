package net.chrisrichardson.ftgo.restaurantservice.main;

import net.chrisrichardson.ftgo.restaurantservice.RestaurantServiceConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(RestaurantServiceConfiguration.class)
public class RestaurantServiceMain {

  public static void main(String[] args) {
    SpringApplication.run(RestaurantServiceMain.class, args);
  }
}
