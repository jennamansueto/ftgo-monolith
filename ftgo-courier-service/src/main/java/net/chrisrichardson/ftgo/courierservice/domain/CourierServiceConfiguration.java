package net.chrisrichardson.ftgo.courierservice.domain;

import net.chrisrichardson.ftgo.courierservice.persistence.CourierEntityRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CourierServiceConfiguration {

  @Bean
  public CourierService courierService(CourierEntityRepository courierEntityRepository) {
    return new CourierService(courierEntityRepository);
  }

}
