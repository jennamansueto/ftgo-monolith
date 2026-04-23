package net.chrisrichardson.ftgo.consumerservice.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ConsumerServiceProxyConfiguration {

  @Bean
  public RestTemplate consumerServiceRestTemplate() {
    return new RestTemplate();
  }

  @Bean
  public ConsumerServiceProxy consumerServiceProxy(
          @Value("${consumer.service.url:http://localhost:8082}") String consumerServiceUrl,
          RestTemplate consumerServiceRestTemplate) {
    return new ConsumerServiceProxy(consumerServiceUrl, consumerServiceRestTemplate);
  }
}
