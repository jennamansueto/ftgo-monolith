package net.chrisrichardson.ftgo.consumerservice.standalone.config;

import net.chrisrichardson.ftgo.common.MoneyModule;
import net.chrisrichardson.ftgo.consumerservice.standalone.domain.ConsumerRepository;
import net.chrisrichardson.ftgo.consumerservice.standalone.domain.ConsumerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConsumerServiceConfig {

  @Bean
  public MoneyModule moneyModule() {
    return new MoneyModule();
  }

  @Bean
  public ConsumerService consumerService(ConsumerRepository consumerRepository) {
    return new ConsumerService(consumerRepository);
  }
}
