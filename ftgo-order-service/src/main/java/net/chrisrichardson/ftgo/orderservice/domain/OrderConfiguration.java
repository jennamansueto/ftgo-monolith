package net.chrisrichardson.ftgo.orderservice.domain;

import io.micrometer.core.instrument.MeterRegistry;
import net.chrisrichardson.ftgo.consumerservice.domain.ConsumerService;
import net.chrisrichardson.ftgo.domain.DomainConfiguration;
import net.chrisrichardson.ftgo.domain.OrderRepository;
import net.chrisrichardson.ftgo.domain.RestaurantRepository;
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
  // TODO move to framework
  @Bean
  public OrderService orderService(RestaurantRepository restaurantRepository,
                                   OrderRepository orderRepository,
                                   Optional<MeterRegistry> meterRegistry,
                                   ConsumerService consumerService) {
    return new OrderService(orderRepository,
            restaurantRepository,
            meterRegistry,
            consumerService);
  }

  @Bean
  public RestTemplate courierServiceRestTemplate(RestTemplateBuilder restTemplateBuilder) {
    return restTemplateBuilder
            .setConnectTimeout(5 * 1000)
            .setReadTimeout(10 * 1000)
            .build();
  }

  @Bean
  public CourierServiceClient courierServiceClient(RestTemplate courierServiceRestTemplate,
                                                   @Value("${courier.service.url:http://courier-service:8084}") String courierServiceUrl) {
    return new CourierServiceClient(courierServiceRestTemplate, courierServiceUrl);
  }

  @Bean
  public OrderDeliveryService orderDeliveryService(CourierServiceClient courierServiceClient, OrderService orderService) {
    return new OrderDeliveryService(courierServiceClient, orderService);
  }

  @Bean
  public MeterRegistryCustomizer meterRegistryCustomizer(@Value("${spring.application.name}") String serviceName) {
    return registry -> registry.config().commonTags("service", serviceName);
  }
}
