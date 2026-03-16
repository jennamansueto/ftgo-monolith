package net.chrisrichardson.ftgo.orderservice.domain;

import io.micrometer.core.instrument.MeterRegistry;
import net.chrisrichardson.ftgo.domain.CourierRepository;
import net.chrisrichardson.ftgo.domain.DomainConfiguration;
import net.chrisrichardson.ftgo.domain.OrderRepository;
import net.chrisrichardson.ftgo.domain.RestaurantRepository;
import net.chrisrichardson.ftgo.orderservice.client.ConsumerServiceClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.chrisrichardson.ftgo.common.MoneyModule;

import java.util.Optional;

@Configuration
@Import(DomainConfiguration.class)
public class OrderConfiguration {

  @Bean
  public RestTemplate consumerServiceRestTemplate() {
    SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
    factory.setConnectTimeout(5000);
    factory.setReadTimeout(10000);
    RestTemplate restTemplate = new RestTemplate(factory);
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new MoneyModule());
    MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(objectMapper);
    restTemplate.getMessageConverters().removeIf(c -> c instanceof MappingJackson2HttpMessageConverter);
    restTemplate.getMessageConverters().add(converter);
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
                                   OrderPersistenceService orderPersistenceService,
                                   CourierRepository courierRepository) {
    return new OrderService(orderRepository,
            restaurantRepository,
            meterRegistry,
            consumerServiceClient, orderPersistenceService, courierRepository);
  }

  @Bean
  public MeterRegistryCustomizer meterRegistryCustomizer(@Value("${spring.application.name}") String serviceName) {
    return registry -> registry.config().commonTags("service", serviceName);
  }
}
