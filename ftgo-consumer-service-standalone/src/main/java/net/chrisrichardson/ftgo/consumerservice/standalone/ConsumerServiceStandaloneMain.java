package net.chrisrichardson.ftgo.consumerservice.standalone;

import net.chrisrichardson.ftgo.consumerservice.main.ConsumerServiceConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableAutoConfiguration
@Import(ConsumerServiceConfiguration.class)
public class ConsumerServiceStandaloneMain {

  public static void main(String[] args) {
    SpringApplication.run(ConsumerServiceStandaloneMain.class, args);
  }
}
