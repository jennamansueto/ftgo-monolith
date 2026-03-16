package net.chrisrichardson.ftgo.consumerservice.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.chrisrichardson.ftgo.common.MoneyModule;
import net.chrisrichardson.ftgo.consumerservice.api.ConsumerServiceInterface;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ConsumerConfiguration {

  @Bean
  public RestTemplate consumerRestTemplate() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new MoneyModule());
    MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(objectMapper);
    RestTemplate restTemplate = new RestTemplate();
    restTemplate.getMessageConverters().removeIf(c -> c instanceof MappingJackson2HttpMessageConverter);
    restTemplate.getMessageConverters().add(converter);
    return restTemplate;
  }

  @Bean
  public ConsumerServiceInterface consumerServiceProxy(RestTemplate consumerRestTemplate,
                                                       @Value("${consumer.service.url}") String consumerServiceUrl) {
    return new ConsumerServiceProxy(consumerRestTemplate, consumerServiceUrl);
  }
}
