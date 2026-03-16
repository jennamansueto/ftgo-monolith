package net.chrisrichardson.ftgo.consumerservice.domain;

import net.chrisrichardson.ftgo.consumerservice.api.ConsumerServiceInterface;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ConsumerConfiguration {

  @Bean
  public RestTemplate consumerRestTemplate() {
    return new RestTemplate();
  }

  @Bean
  public ConsumerServiceInterface consumerServiceProxy(RestTemplate consumerRestTemplate,
                                                       @Value("${consumer.service.url}") String consumerServiceUrl) {
    return new ConsumerServiceProxy(consumerRestTemplate, consumerServiceUrl);
  }
}
