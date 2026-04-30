package net.chrisrichardson.ftgo.consumerservice;

import net.chrisrichardson.ftgo.consumerservice.main.ConsumerServiceConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(ConsumerServiceConfiguration.class)
public class FtgoConsumerServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(FtgoConsumerServiceApplication.class, args);
  }
}
