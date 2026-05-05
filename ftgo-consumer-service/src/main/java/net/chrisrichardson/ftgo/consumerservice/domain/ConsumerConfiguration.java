package net.chrisrichardson.ftgo.consumerservice.domain;

import net.chrisrichardson.ftgo.domain.DomainConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

@Configuration
@Import(DomainConfiguration.class)
public class ConsumerConfiguration {

  /**
   * Marked {@code @Primary} so that when this configuration is loaded into the
   * same Spring context as {@code OrderConfiguration} (e.g. the in-process
   * {@code FtgoApplicationTest} integration test), the local in-process
   * {@link ConsumerService} bean is preferred over the HTTP-based
   * {@code ConsumerServiceProxy} that the order service also exposes as a
   * {@code ConsumerServiceAPI} bean. In production the consumer service is no
   * longer on the monolith classpath so there is only one candidate.
   */
  @Bean
  @Primary
  public ConsumerService consumerService() {
    return new ConsumerService();
  }
}
