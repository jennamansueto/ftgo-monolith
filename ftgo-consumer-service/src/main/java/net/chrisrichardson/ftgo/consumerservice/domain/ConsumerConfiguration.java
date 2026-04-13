package net.chrisrichardson.ftgo.consumerservice.domain;

import net.chrisrichardson.ftgo.common.CommonConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackageClasses = ConsumerRepository.class)
@EntityScan(basePackageClasses = Consumer.class)
@Import(CommonConfiguration.class)
public class ConsumerConfiguration {

  @Bean
  public ConsumerService consumerService(ConsumerRepository consumerRepository) {
    return new ConsumerService(consumerRepository);
  }
}
