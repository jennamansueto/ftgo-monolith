package net.chrisrichardson.ftgo.orderservice.consumerclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ConsumerServiceClientConfiguration {

  @Bean
  public RestTemplate consumerServiceRestTemplate() {
    SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
    factory.setConnectTimeout(5000);
    factory.setReadTimeout(10000);
    return new RestTemplate(factory);
  }

  @Bean
  public ConsumerServiceClient consumerServiceClient(
          RestTemplate consumerServiceRestTemplate,
          @Value("${consumer.service.url:http://consumer-service:8082}") String consumerServiceBaseUrl) {
    return new ConsumerServiceClient(consumerServiceRestTemplate, consumerServiceBaseUrl);
  }
}
