package net.chrisrichardson.ftgo.consumerservice.client;

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
  public ConsumerService consumerServiceProxy(
          @Value("${ftgo.consumer-service.base-url:http://localhost:8082}") String baseUrl,
          RestTemplate consumerServiceRestTemplate) {
    return new ConsumerServiceProxy(baseUrl, consumerServiceRestTemplate);
  }
}
