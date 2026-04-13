package net.chrisrichardson.ftgo.consumerservice.client;

import net.chrisrichardson.ftgo.consumerservice.api.ConsumerValidationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ConsumerServiceClientConfiguration {

  @Bean
  public RestTemplate consumerServiceRestTemplate() {
    return new RestTemplate();
  }

  @Bean
  public ConsumerValidationService consumerValidationService(
          @Value("${consumer.service.url}") String consumerServiceUrl,
          RestTemplate consumerServiceRestTemplate) {
    return new ConsumerServiceProxy(consumerServiceUrl, consumerServiceRestTemplate);
  }
}
