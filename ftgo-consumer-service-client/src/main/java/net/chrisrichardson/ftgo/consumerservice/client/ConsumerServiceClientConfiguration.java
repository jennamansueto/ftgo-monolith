package net.chrisrichardson.ftgo.consumerservice.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.chrisrichardson.ftgo.common.CommonJsonMapperInitializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ConsumerServiceClientConfiguration {

  @Bean
  public CommonJsonMapperInitializer commonJsonMapperInitializer() {
    return new CommonJsonMapperInitializer();
  }

  @Bean
  public RestTemplate consumerServiceRestTemplate(ObjectMapper objectMapper) {
    RestTemplate restTemplate = new RestTemplate();
    restTemplate.getMessageConverters().stream()
            .filter(MappingJackson2HttpMessageConverter.class::isInstance)
            .map(MappingJackson2HttpMessageConverter.class::cast)
            .forEach(converter -> converter.setObjectMapper(objectMapper));
    return restTemplate;
  }

  @Bean
  public ConsumerService consumerServiceProxy(
          @Value("${ftgo.consumer-service.base-url:http://localhost:8082}") String baseUrl,
          RestTemplate consumerServiceRestTemplate) {
    return new ConsumerServiceProxy(baseUrl, consumerServiceRestTemplate);
  }
}
