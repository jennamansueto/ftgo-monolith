package net.chrisrichardson.ftgo.courierservice.main;

import net.chrisrichardson.ftgo.courierservice.web.CourierWebConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableAutoConfiguration
@Import(CourierWebConfiguration.class)
public class CourierServiceMain {

  public static void main(String[] args) {
    SpringApplication.run(CourierServiceMain.class, args);
  }
}
