package net.chrisrichardson.ftgo.consumerservice.main;

import net.chrisrichardson.ftgo.consumerservice.domain.ConsumerConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(ConsumerConfiguration.class)
public class ConsumerServiceConfiguration {
}
