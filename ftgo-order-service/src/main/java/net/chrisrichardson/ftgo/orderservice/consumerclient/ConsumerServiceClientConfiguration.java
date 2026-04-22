package net.chrisrichardson.ftgo.orderservice.consumerclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ConsumerServiceClientConfiguration {

  @Bean
  public RestTemplate consumerServiceRestTemplate(RestTemplateBuilder builder) {
    // Build via RestTemplateBuilder so the auto-configured MappingJackson2HttpMessageConverter
    // (which uses the application ObjectMapper with MoneyModule registered) is applied. A
    // plain new RestTemplate() would serialize Money with a default ObjectMapper that lacks
    // MoneyModule and fail on every call.
    return builder
            .setConnectTimeout(5000)
            .setReadTimeout(10000)
            .build();
  }

  @Bean
  public ConsumerServiceClient consumerServiceClient(
          RestTemplate consumerServiceRestTemplate,
          @Value("${consumer.service.url:http://consumer-service:8082}") String consumerServiceBaseUrl) {
    return new ConsumerServiceClient(consumerServiceRestTemplate, consumerServiceBaseUrl);
  }
}
