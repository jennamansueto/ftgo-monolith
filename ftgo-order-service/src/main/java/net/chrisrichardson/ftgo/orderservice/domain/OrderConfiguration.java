package net.chrisrichardson.ftgo.orderservice.domain;

import io.micrometer.core.instrument.MeterRegistry;
import net.chrisrichardson.ftgo.consumerservice.domain.ConsumerService;
import net.chrisrichardson.ftgo.domain.CourierRepository;
import net.chrisrichardson.ftgo.domain.DomainConfiguration;
import net.chrisrichardson.ftgo.domain.OrderRepository;
import net.chrisrichardson.ftgo.common.MoneyModule;
import net.chrisrichardson.ftgo.orderservice.client.RestaurantServiceClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Configuration
@Import(DomainConfiguration.class)
public class OrderConfiguration {

  @Bean
  public RestTemplate restaurantServiceRestTemplate() {
    SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
    factory.setConnectTimeout(5000);
    factory.setReadTimeout(10000);
    RestTemplate restTemplate = new RestTemplate(factory);

    // Register MoneyModule so the RestTemplate can deserialize Money fields
    // (e.g. MenuItemDTO.price) serialized as plain strings by the restaurant service.
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new MoneyModule());
    objectMapper.registerModule(new JavaTimeModule());
    MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(objectMapper);
    restTemplate.getMessageConverters().removeIf(c -> c instanceof MappingJackson2HttpMessageConverter);
    restTemplate.getMessageConverters().add(converter);

    return restTemplate;
  }

  @Bean
  public RestaurantServiceClient restaurantServiceClient(
          RestTemplate restaurantServiceRestTemplate,
          @Value("${restaurant.service.url:http://localhost:8083}") String restaurantServiceUrl) {
    return new RestaurantServiceClient(restaurantServiceRestTemplate, restaurantServiceUrl);
  }

  @Bean
  public OrderCreationService orderCreationService(OrderRepository orderRepository,
                                                    Optional<MeterRegistry> meterRegistry,
                                                    ConsumerService consumerService) {
    return new OrderCreationService(orderRepository, meterRegistry, consumerService);
  }

  @Bean
  public OrderService orderService(OrderRepository orderRepository,
                                   Optional<MeterRegistry> meterRegistry,
                                   ConsumerService consumerService,
                                   CourierRepository courierRepository,
                                   RestaurantServiceClient restaurantServiceClient,
                                   OrderCreationService orderCreationService) {
    return new OrderService(orderRepository,
            meterRegistry,
            consumerService,
            courierRepository,
            restaurantServiceClient,
            orderCreationService);
  }

  @Bean
  public MeterRegistryCustomizer meterRegistryCustomizer(@Value("${spring.application.name}") String serviceName) {
    return registry -> registry.config().commonTags("service", serviceName);
  }
}
