package net.chrisrichardson.ftgo.orderservice.proxy;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.chrisrichardson.ftgo.consumerservice.api.ConsumerServiceInterface;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ConsumerServiceProxyConfiguration {

  @Bean
  @ConditionalOnMissingBean(ConsumerServiceInterface.class)
  public RestTemplate consumerServiceRestTemplate(ObjectMapper objectMapper) {
    RestTemplate restTemplate = new RestTemplate();
    MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(objectMapper);
    restTemplate.getMessageConverters().removeIf(c -> c instanceof MappingJackson2HttpMessageConverter);
    restTemplate.getMessageConverters().add(converter);
    return restTemplate;
  }

  @Bean
  @ConditionalOnMissingBean(ConsumerServiceInterface.class)
  public ConsumerServiceInterface consumerServiceProxy(RestTemplate consumerServiceRestTemplate,
                                                        @Value("${consumer.service.url:http://localhost:8082}") String consumerServiceUrl) {
    return new ConsumerServiceProxy(consumerServiceRestTemplate, consumerServiceUrl);
  }
}
