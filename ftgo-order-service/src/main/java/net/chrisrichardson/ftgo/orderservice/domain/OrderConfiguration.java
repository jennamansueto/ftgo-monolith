package net.chrisrichardson.ftgo.orderservice.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.MeterRegistry;
import net.chrisrichardson.ftgo.domain.CourierRepository;
import net.chrisrichardson.ftgo.domain.DomainConfiguration;
import net.chrisrichardson.ftgo.domain.OrderRepository;
import net.chrisrichardson.ftgo.domain.RestaurantRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Configuration
@Import(DomainConfiguration.class)
public class OrderConfiguration {

  @Bean
  public RestTemplate consumerServiceRestTemplate(ObjectMapper objectMapper) {
    RestTemplate restTemplate = new RestTemplate();
    restTemplate.getMessageConverters().stream()
            .filter(c -> c instanceof MappingJackson2HttpMessageConverter)
            .forEach(c -> ((MappingJackson2HttpMessageConverter) c).setObjectMapper(objectMapper));
    return restTemplate;
  }

  @Bean
  @Lazy
  public ConsumerServiceProxy consumerServiceProxy(
          RestTemplate consumerServiceRestTemplate,
          @Value("${consumer.service.url:http://localhost:8082}") String consumerServiceUrl) {
    return new ConsumerServiceProxy(consumerServiceRestTemplate, consumerServiceUrl);
  }

  @Bean
  public OrderService orderService(RestaurantRepository restaurantRepository,
                                   OrderRepository orderRepository,
                                   Optional<MeterRegistry> meterRegistry,
                                   ConsumerServiceProxy consumerServiceProxy, CourierRepository courierRepository) {
    return new OrderService(orderRepository,
            restaurantRepository,
            meterRegistry,
            consumerServiceProxy, courierRepository);
  }

  @Bean
  public MeterRegistryCustomizer meterRegistryCustomizer(@Value("${spring.application.name}") String serviceName) {
    return registry -> registry.config().commonTags("service", serviceName);
  }
}
