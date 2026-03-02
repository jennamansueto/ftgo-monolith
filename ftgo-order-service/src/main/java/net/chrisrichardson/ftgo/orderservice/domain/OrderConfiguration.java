package net.chrisrichardson.ftgo.orderservice.domain;

import io.micrometer.core.instrument.MeterRegistry;
import net.chrisrichardson.ftgo.domain.CourierRepository;
import net.chrisrichardson.ftgo.domain.DomainConfiguration;
import net.chrisrichardson.ftgo.domain.OrderRepository;
import net.chrisrichardson.ftgo.domain.RestaurantRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.chrisrichardson.ftgo.orderservice.client.ConsumerServiceClient;
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
  public RestTemplate consumerServiceRestTemplate(ObjectMapper objectMapper) {
    SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
    factory.setConnectTimeout(5000);
    factory.setReadTimeout(10000);
    RestTemplate restTemplate = new RestTemplate(factory);
    restTemplate.getMessageConverters().stream()
        .filter(c -> c instanceof MappingJackson2HttpMessageConverter)
        .map(c -> (MappingJackson2HttpMessageConverter) c)
        .forEach(c -> c.setObjectMapper(objectMapper));
    return restTemplate;
  }

  @Bean
  public ConsumerServiceClient consumerServiceClient(
          RestTemplate consumerServiceRestTemplate,
          @Value("${consumer.service.url}") String consumerServiceUrl) {
    return new ConsumerServiceClient(consumerServiceRestTemplate, consumerServiceUrl);
  }

  @Bean
  public OrderPersistenceService orderPersistenceService(OrderRepository orderRepository,
                                                         Optional<MeterRegistry> meterRegistry) {
    return new OrderPersistenceService(orderRepository, meterRegistry);
  }

  @Bean
  public OrderService orderService(RestaurantRepository restaurantRepository,
                                   OrderRepository orderRepository,
                                   Optional<MeterRegistry> meterRegistry,
                                   ConsumerServiceClient consumerServiceClient,
                                   CourierRepository courierRepository,
                                   OrderPersistenceService orderPersistenceService) {
    return new OrderService(orderRepository,
            restaurantRepository,
            meterRegistry,
            consumerServiceClient, courierRepository, orderPersistenceService);
  }

  @Bean
  public MeterRegistryCustomizer meterRegistryCustomizer(@Value("${spring.application.name}") String serviceName) {
    return registry -> registry.config().commonTags("service", serviceName);
  }
}
