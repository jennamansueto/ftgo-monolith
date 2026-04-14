package net.chrisrichardson.ftgo.orderservice.proxy;

import net.chrisrichardson.ftgo.consumerservice.api.ConsumerServiceInterface;
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
  public ConsumerServiceInterface consumerServiceProxy(RestTemplate consumerServiceRestTemplate,
                                                        @Value("${consumer.service.url:http://localhost:8082}") String consumerServiceUrl) {
    return new ConsumerServiceProxy(consumerServiceRestTemplate, consumerServiceUrl);
  }
}
