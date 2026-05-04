package net.chrisrichardson.ftgo.orderservice.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.MeterRegistry;
import net.chrisrichardson.ftgo.common.MoneyModule;
import net.chrisrichardson.ftgo.consumerservice.domain.ConsumerService;
import net.chrisrichardson.ftgo.domain.CourierRepository;
import net.chrisrichardson.ftgo.domain.DomainConfiguration;
import net.chrisrichardson.ftgo.domain.OrderRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

import java.util.Optional;

@Configuration
@Import(DomainConfiguration.class)
public class OrderConfiguration {

  @Bean
  public OrderService orderService(OrderRepository orderRepository,
                                   Optional<MeterRegistry> meterRegistry,
                                   ConsumerService consumerService, CourierRepository courierRepository) {
    return new OrderService(orderRepository,
            meterRegistry,
            consumerService, courierRepository);
  }

  @Bean
  public RestaurantServiceClient restaurantServiceClient(RestTemplate restaurantServiceRestTemplate,
                                                         @Value("${restaurant.service.url:http://localhost:8083}") String restaurantServiceUrl) {
    return new RestaurantServiceClient(restaurantServiceRestTemplate, restaurantServiceUrl);
  }

  @Bean
  public RestTemplate restaurantServiceRestTemplate() {
    RestTemplate restTemplate = new RestTemplate();
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new MoneyModule());
    MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(objectMapper);
    restTemplate.setMessageConverters(Collections.singletonList(converter));
    return restTemplate;
  }

  @Bean
  public MeterRegistryCustomizer meterRegistryCustomizer(@Value("${spring.application.name}") String serviceName) {
    return registry -> registry.config().commonTags("service", serviceName);
  }
}
