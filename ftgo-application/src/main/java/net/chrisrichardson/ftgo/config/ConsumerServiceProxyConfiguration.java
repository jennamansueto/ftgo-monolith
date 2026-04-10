package net.chrisrichardson.ftgo.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.chrisrichardson.ftgo.consumerservice.api.ConsumerServiceInterface;
import net.chrisrichardson.ftgo.consumerservice.api.ConsumerServiceProxy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Configuration
public class ConsumerServiceProxyConfiguration {

  @Bean
  public RestTemplate consumerServiceRestTemplate(ObjectMapper objectMapper) {
    RestTemplate restTemplate = new RestTemplate();
    MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(objectMapper);
    restTemplate.setMessageConverters(Collections.singletonList(converter));
    return restTemplate;
  }

  @Bean
  public ConsumerServiceInterface consumerService(
          RestTemplate consumerServiceRestTemplate,
          @Value("${consumer.service.url:http://localhost:8082}") String consumerServiceUrl) {
    return new ConsumerServiceProxy(consumerServiceRestTemplate, consumerServiceUrl);
  }
}
