package net.chrisrichardson.ftgo.orderservice.domain;

import io.micrometer.core.instrument.MeterRegistry;
import net.chrisrichardson.ftgo.domain.CourierRepository;
import net.chrisrichardson.ftgo.domain.DomainConfiguration;
import net.chrisrichardson.ftgo.domain.OrderRepository;
import net.chrisrichardson.ftgo.domain.RestaurantRepository;
import net.chrisrichardson.ftgo.orderservice.consumerclient.ConsumerServiceClient;
import net.chrisrichardson.ftgo.orderservice.consumerclient.ConsumerServiceClientConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Optional;

@Configuration
@Import({DomainConfiguration.class, ConsumerServiceClientConfiguration.class})
public class OrderConfiguration {
  // TODO move to framework
  @Bean
  public OrderService orderService(RestaurantRepository restaurantRepository,
                                   OrderRepository orderRepository,
                                   Optional<MeterRegistry> meterRegistry,
                                   ConsumerServiceClient consumerServiceClient,
                                   CourierRepository courierRepository,
                                   PlatformTransactionManager transactionManager) {
    return new OrderService(orderRepository,
            restaurantRepository,
            meterRegistry,
            consumerServiceClient,
            courierRepository,
            transactionManager);
  }

  @Bean
  public MeterRegistryCustomizer meterRegistryCustomizer(@Value("${spring.application.name}") String serviceName) {
    return registry -> registry.config().commonTags("service", serviceName);
  }
}
