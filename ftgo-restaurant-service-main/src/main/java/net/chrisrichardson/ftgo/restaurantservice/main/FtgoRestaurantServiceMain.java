package net.chrisrichardson.ftgo.restaurantservice.main;

import net.chrisrichardson.ftgo.restaurantservice.RestaurantServiceConfiguration;
import net.chrisrichardson.ftgo.restaurantservice.domain.Restaurant;
import net.chrisrichardson.ftgo.restaurantservice.domain.RestaurantRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@Import(RestaurantServiceConfiguration.class)
@EntityScan(basePackageClasses = Restaurant.class)
@EnableJpaRepositories(basePackageClasses = RestaurantRepository.class)
public class FtgoRestaurantServiceMain {

  public static void main(String[] args) {
    SpringApplication.run(FtgoRestaurantServiceMain.class, args);
  }
}
