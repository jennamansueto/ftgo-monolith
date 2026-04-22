package net.chrisrichardson.ftgo.orderservice.courier;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class CourierServiceClientConfiguration {

  @Bean
  public RestTemplate courierServiceRestTemplate(RestTemplateBuilder builder) {
    return builder
            .setConnectTimeout(5000)
            .setReadTimeout(10000)
            .build();
  }

  @Bean
  public CourierServiceClient courierServiceClient(RestTemplate courierServiceRestTemplate,
                                                   @Value("${courier.service.url:http://courier-service:8084}") String baseUrl) {
    return new CourierServiceClient(courierServiceRestTemplate, baseUrl);
  }
}
