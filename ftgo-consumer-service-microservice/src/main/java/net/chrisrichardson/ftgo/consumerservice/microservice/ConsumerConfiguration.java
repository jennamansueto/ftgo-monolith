package net.chrisrichardson.ftgo.consumerservice.microservice;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConsumerConfiguration {

  @Bean
  public ConsumerMicroservice consumerMicroservice(ConsumerEntityRepository consumerRepository) {
    return new ConsumerMicroservice(consumerRepository);
  }
}
