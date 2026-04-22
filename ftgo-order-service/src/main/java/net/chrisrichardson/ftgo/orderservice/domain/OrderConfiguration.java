package net.chrisrichardson.ftgo.orderservice.domain;

import io.micrometer.core.instrument.MeterRegistry;
import net.chrisrichardson.ftgo.consumerservice.domain.ConsumerService;
import net.chrisrichardson.ftgo.domain.CourierRepository;
import net.chrisrichardson.ftgo.domain.DomainConfiguration;
import net.chrisrichardson.ftgo.domain.OrderRepository;
import net.chrisrichardson.ftgo.orderservice.restaurantclient.RestaurantServiceClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Configuration
@Import(DomainConfiguration.class)
public class OrderConfiguration {

  @Bean
  public OrderService orderService(RestaurantServiceClient restaurantServiceClient,
                                   OrderRepository orderRepository,
                                   Optional<MeterRegistry> meterRegistry,
                                   ConsumerService consumerService, CourierRepository courierRepository) {
    return new OrderService(orderRepository,
            restaurantServiceClient,
            meterRegistry,
            consumerService, courierRepository);
  }

  @Bean
  public RestTemplate restaurantServiceRestTemplate(RestTemplateBuilder builder) {
    return builder
            .setConnectTimeout(5000)
            .setReadTimeout(10000)
            .build();
  }

  @Bean
  public RestaurantServiceClient restaurantServiceClient(RestTemplate restaurantServiceRestTemplate,
                                                         @Value("${restaurant.service.url:http://restaurant-service:8083}") String baseUrl) {
    return new RestaurantServiceClient(restaurantServiceRestTemplate, baseUrl);
  }

  @Bean
  public MeterRegistryCustomizer meterRegistryCustomizer(@Value("${spring.application.name}") String serviceName) {
    return registry -> registry.config().commonTags("service", serviceName);
  }
}
