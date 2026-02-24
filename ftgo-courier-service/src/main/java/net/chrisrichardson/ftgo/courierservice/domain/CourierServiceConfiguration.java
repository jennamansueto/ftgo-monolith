package net.chrisrichardson.ftgo.courierservice.domain;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan(basePackageClasses = Courier.class)
@EnableJpaRepositories(basePackageClasses = CourierRepository.class)
public class CourierServiceConfiguration {

  @Bean
  public CourierService courierService(CourierRepository courierRepository) {
    return new CourierService(courierRepository);
  }

}
