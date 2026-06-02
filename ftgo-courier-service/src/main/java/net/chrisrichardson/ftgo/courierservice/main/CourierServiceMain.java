package net.chrisrichardson.ftgo.courierservice.main;

import net.chrisrichardson.ftgo.courierservice.domain.Courier;
import net.chrisrichardson.ftgo.courierservice.domain.CourierRepository;
import net.chrisrichardson.ftgo.courierservice.web.CourierWebConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackageClasses = Courier.class)
@EnableJpaRepositories(basePackageClasses = CourierRepository.class)
@Import(CourierWebConfiguration.class)
public class CourierServiceMain {

  public static void main(String[] args) {
    SpringApplication.run(CourierServiceMain.class, args);
  }
}
