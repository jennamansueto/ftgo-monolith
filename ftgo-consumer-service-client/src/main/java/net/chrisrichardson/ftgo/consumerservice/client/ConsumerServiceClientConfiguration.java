package net.chrisrichardson.ftgo.consumerservice.client;

import net.chrisrichardson.ftgo.consumerservice.api.ConsumerServiceProxy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ConsumerServiceClientConfiguration {

    @Bean
    public RestTemplate consumerServiceRestTemplate() {
        return new RestTemplateBuilder()
                .setConnectTimeout(5000)
                .setReadTimeout(5000)
                .build();
    }

    @Bean
    public ConsumerServiceProxy consumerServiceProxy(
            RestTemplate consumerServiceRestTemplate,
            @Value("${consumer.service.url:http://localhost:8082}") String consumerServiceUrl) {
        return new ConsumerServiceRestClient(consumerServiceRestTemplate, consumerServiceUrl);
    }
}
