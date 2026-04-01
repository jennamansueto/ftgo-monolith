package net.chrisrichardson.ftgo.orderservice.domain.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ConsumerServiceClientConfiguration {

  @Bean
  public RestTemplate consumerServiceRestTemplate(RestTemplateBuilder builder) {
    return builder
            .setConnectTimeout(5000)
            .setReadTimeout(5000)
            .build();
  }

  @Bean
  public ConsumerServiceClient consumerServiceClient(
          RestTemplate consumerServiceRestTemplate,
          @Value("${consumer.service.url:http://localhost:8082}") String consumerServiceUrl) {
    return new ConsumerServiceProxy(consumerServiceRestTemplate, consumerServiceUrl);
  }
}
