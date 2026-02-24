package net.chrisrichardson.ftgo.courierservice;

import net.chrisrichardson.ftgo.courierservice.web.CourierWebConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableAutoConfiguration
@ComponentScan
@Import(CourierWebConfiguration.class)
public class CourierServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(CourierServiceApplication.class, args);
  }
}
