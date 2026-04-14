package net.chrisrichardson.ftgo.consumerservice.standalone;

import net.chrisrichardson.ftgo.common.CommonConfiguration;
import net.chrisrichardson.ftgo.consumerservice.domain.ConsumerConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({ConsumerConfiguration.class, CommonConfiguration.class})
public class ConsumerServiceStandaloneConfiguration {
}
