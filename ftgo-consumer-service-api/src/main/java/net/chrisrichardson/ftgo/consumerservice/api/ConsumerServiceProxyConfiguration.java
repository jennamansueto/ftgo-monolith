package net.chrisrichardson.ftgo.consumerservice.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ConsumerServiceProxyConfiguration {

  @Bean
  public ConsumerServiceClient consumerServiceClient(
          @Value("${consumer.service.url}") String consumerServiceUrl) {
    return new ConsumerServiceProxy(consumerServiceRestTemplate(), consumerServiceUrl);
  }

  @Bean
  public RestTemplate consumerServiceRestTemplate() {
    SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
    factory.setConnectTimeout(5000);
    factory.setReadTimeout(5000);
    return new RestTemplate(factory);
  }
}
