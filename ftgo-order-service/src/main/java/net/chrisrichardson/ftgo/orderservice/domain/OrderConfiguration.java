package net.chrisrichardson.ftgo.orderservice.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.MeterRegistry;
import net.chrisrichardson.ftgo.consumerservice.api.ConsumerServiceAPI;
import net.chrisrichardson.ftgo.domain.CourierRepository;
import net.chrisrichardson.ftgo.domain.DomainConfiguration;
import net.chrisrichardson.ftgo.domain.OrderRepository;
import net.chrisrichardson.ftgo.domain.RestaurantRepository;
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
  // TODO move to framework
  @Bean
  public OrderService orderService(RestaurantRepository restaurantRepository,
                                   OrderRepository orderRepository,
                                   Optional<MeterRegistry> meterRegistry,
                                   ConsumerServiceAPI consumerService, CourierRepository courierRepository) {
    return new OrderService(orderRepository,
            restaurantRepository,
            meterRegistry,
            consumerService, courierRepository);
  }

  /**
   * RestTemplate used by {@link ConsumerServiceProxy}. Wired with the
   * Spring-managed {@link ObjectMapper} so that the {@code MoneyModule}
   * registered by {@code CommonJsonMapperInitializer} is honored — without
   * this, {@code Money} (whose {@code amount} field is private with no public
   * getter) would serialize to {@code &#123;&#125;} and the consumer service
   * would fail to deserialize the validate request body.
   */
  @Bean
  public RestTemplate consumerServiceRestTemplate(ObjectMapper objectMapper) {
    MappingJackson2HttpMessageConverter jacksonConverter = new MappingJackson2HttpMessageConverter(objectMapper);
    RestTemplate restTemplate = new RestTemplate();
    restTemplate.setMessageConverters(Collections.singletonList(jacksonConverter));
    return restTemplate;
  }

  @Bean
  public ConsumerServiceAPI consumerServiceProxy(@Value("${consumer.service.url:http://localhost:8082}") String consumerServiceUrl,
                                                 RestTemplate consumerServiceRestTemplate) {
    return new ConsumerServiceProxy(consumerServiceUrl, consumerServiceRestTemplate);
  }

  @Bean
  public MeterRegistryCustomizer meterRegistryCustomizer(@Value("${spring.application.name}") String serviceName) {
    return registry -> registry.config().commonTags("service", serviceName);
  }
}
