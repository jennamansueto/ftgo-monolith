package net.chrisrichardson.ftgo.courierservice.domain;

import net.chrisrichardson.ftgo.domain.CourierRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(CourierDomainConfiguration.class)
public class CourierServiceConfiguration {

  @Bean
  public CourierService courierService(CourierRepository courierRepository) {
    return new CourierService(courierRepository);
  }

}
