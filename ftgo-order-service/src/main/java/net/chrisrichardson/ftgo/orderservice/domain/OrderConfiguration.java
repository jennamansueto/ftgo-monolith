package net.chrisrichardson.ftgo.orderservice.domain;

import io.micrometer.core.instrument.MeterRegistry;
import net.chrisrichardson.ftgo.consumerservice.domain.ConsumerService;
import net.chrisrichardson.ftgo.domain.CourierRepository;
import net.chrisrichardson.ftgo.domain.DomainConfiguration;
import net.chrisrichardson.ftgo.domain.OrderRepository;
import net.chrisrichardson.ftgo.orderservice.client.RestaurantServiceClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
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
    return new RestTemplate(factory);
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
