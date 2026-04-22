package net.chrisrichardson.ftgo.orderservice.domain;

import io.micrometer.core.instrument.MeterRegistry;
import net.chrisrichardson.ftgo.consumerservice.domain.ConsumerService;
import net.chrisrichardson.ftgo.domain.DomainConfiguration;
import net.chrisrichardson.ftgo.domain.OrderRepository;
import net.chrisrichardson.ftgo.domain.RestaurantRepository;
import net.chrisrichardson.ftgo.orderservice.courier.CourierServiceClient;
import net.chrisrichardson.ftgo.orderservice.courier.CourierServiceClientConfiguration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Optional;

@Configuration
@Import({DomainConfiguration.class, CourierServiceClientConfiguration.class})
public class OrderConfiguration {
  // TODO move to framework
  @Bean
  public OrderService orderService(RestaurantRepository restaurantRepository,
                                   OrderRepository orderRepository,
                                   Optional<MeterRegistry> meterRegistry,
                                   ConsumerService consumerService,
                                   CourierServiceClient courierServiceClient,
                                   @Qualifier("orderServiceTransactionTemplate") TransactionTemplate transactionTemplate,
                                   @Qualifier("orderServiceReadOnlyTransactionTemplate") TransactionTemplate readOnlyTransactionTemplate) {
    return new OrderService(orderRepository,
            restaurantRepository,
            meterRegistry,
            consumerService,
            courierServiceClient,
            transactionTemplate,
            readOnlyTransactionTemplate);
  }

  @Bean
  public TransactionTemplate orderServiceTransactionTemplate(PlatformTransactionManager transactionManager) {
    return new TransactionTemplate(transactionManager);
  }

  @Bean
  public TransactionTemplate orderServiceReadOnlyTransactionTemplate(PlatformTransactionManager transactionManager) {
    TransactionTemplate template = new TransactionTemplate(transactionManager);
    template.setReadOnly(true);
    template.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
    return template;
  }

  @Bean
  public MeterRegistryCustomizer meterRegistryCustomizer(@Value("${spring.application.name}") String serviceName) {
    return registry -> registry.config().commonTags("service", serviceName);
  }
}
