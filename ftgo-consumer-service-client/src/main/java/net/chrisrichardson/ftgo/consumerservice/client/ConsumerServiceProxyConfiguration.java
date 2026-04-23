package net.chrisrichardson.ftgo.consumerservice.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.chrisrichardson.ftgo.common.MoneyModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ConsumerServiceProxyConfiguration {

  @Bean
  public RestTemplate consumerServiceRestTemplate() {
    RestTemplate restTemplate = new RestTemplate();
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new MoneyModule());
    MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(objectMapper);
    restTemplate.getMessageConverters().removeIf(c -> c instanceof MappingJackson2HttpMessageConverter);
    restTemplate.getMessageConverters().add(converter);
    return restTemplate;
  }

  @Bean
  public ConsumerServiceProxy consumerServiceProxy(
          @Value("${consumer.service.url:http://localhost:8082}") String consumerServiceUrl,
          RestTemplate consumerServiceRestTemplate) {
    return new ConsumerServiceProxy(consumerServiceUrl, consumerServiceRestTemplate);
  }
}
